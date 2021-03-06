package cn.cerc.jmis.core;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cn.cerc.jbean.client.LocalService;
import cn.cerc.jbean.core.AppConfig;
import cn.cerc.jbean.core.AppHandle;
import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.core.PageException;
import cn.cerc.jbean.form.IForm;
import cn.cerc.jbean.form.IPage;
import cn.cerc.jbean.other.BufferType;
import cn.cerc.jbean.other.MemoryBuffer;
import cn.cerc.jbean.tools.IAppLogin;
import cn.cerc.jdb.core.Record;
import cn.cerc.jmis.form.Webpage;
import cn.cerc.jmis.page.ErrorPage;
import cn.cerc.jmis.page.JspPage;
import cn.cerc.jmis.page.RedirectPage;

public class StartForms implements Filter {

    private static final Logger log = LoggerFactory.getLogger(StartForms.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // 遇到静太文件直接输出
        if (isStatic(uri)) {
            chain.doFilter(req, resp);
            return;
        }
        log.info(uri);

        String childCode = getRequestCode(req);
        if (childCode == null) {
            req.setAttribute("message", "无效的请求：" + childCode);
            req.getRequestDispatcher(Application.getAppConfig().getJspErrorFile()).forward(req, resp);
            return;
        }

        String[] params = childCode.split("\\.");
        String formId = params[0];
        String funcCode = params.length == 1 ? "execute" : params[1];

        req.setAttribute("logon", false);

        // 验证菜单是否启停
        if (Application.containsBean("AppFormFilter")) {
            IFormFilter formFilter = Application.getBean("AppFormFilter", IFormFilter.class);
            if (formFilter != null) {
                if (formFilter.doFilter(resp, formId, funcCode)) {
                    return;
                }
            }
        }

        IForm form = null;
        try {
            form = createForm(req, resp, formId);
            if (form == null) {
                req.setAttribute("message", "error servlet:" + req.getServletPath());
                AppConfig conf = createConfig();
                req.getRequestDispatcher(conf.getJspErrorFile()).forward(req, resp);
                return;
            }

            // 设备讯息
            ClientDevice info = new ClientDevice(form);
            info.setRequest(req);
            req.setAttribute("_showMenu_", !ClientDevice.device_ee.equals(info.getDevice()));
            form.setClient(info);

            // 建立数据库资源
            try (AppHandle handle = createHandle(req)) {
                try {
                    handle.setProperty(Application.sessionId, req.getSession().getId());
                    handle.setProperty(Application.deviceLanguage, info.getLanguage());
                    req.setAttribute("myappHandle", handle);
                    form.setHandle(handle);

                    log.debug("进行安全检查，若未登录则显示登录对话框");

                    IAppLogin page = createLogin(form);
                    if (page.checkSecurity(info.getSid())) {
                        callForm(form, funcCode);
                    }
                } catch (Exception e) {
                    Throwable err = e.getCause();
                    if (err == null) {
                        err = e;
                    }
                    // 重定向到错误页面
                    req.setAttribute("msg", err.getMessage());
                    ErrorPage opera = new ErrorPage(form, err);
                    opera.execute();
                }
            }
        } catch (Exception e) {
            log.error(childCode + ":" + e.getMessage());
            req.setAttribute("message", e.getMessage());
            AppConfig conf = Application.getAppConfig();
            // 重定向到错误页面
            req.getRequestDispatcher(conf.getJspErrorFile()).forward(req, resp);
            return;
        }
    }

    // 创建登录与权限控制器
    protected IAppLogin createLogin(IForm form) {
        return Application.getAppLogin(form);
    }

    // 创建环境管理控制器
    protected AppHandle createHandle(HttpServletRequest req) {
        return new AppHandle();
    }

    // 取得页面默认设置，如出错时指向哪个页面
    protected AppConfig createConfig() {
        return Application.getAppConfig();
    }

    // 取得要执行的页面控制器
    protected IForm createForm(HttpServletRequest req, HttpServletResponse resp, String formId) {
        return Application.getForm(req, resp, formId);
    }

    // 判断请求的是否为静态文件
    protected boolean isStatic(String uri) {
        if (uri.endsWith(".css") || uri.endsWith(".jpg") || uri.endsWith(".gif") || uri.endsWith(".png")
                || uri.endsWith(".bmp") || uri.endsWith(".js") || uri.endsWith(".mp3") || uri.endsWith(".icon")
                || uri.endsWith(".apk") || uri.endsWith(".exe") || uri.endsWith(".jsp") || uri.endsWith(".htm")
                || uri.endsWith(".html") || uri.endsWith(".manifest")) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean checkEnableTime() {
        // Calendar cal = Calendar.getInstance();
        // 月底最后一天
        // if (TDate.Today().compareTo(TDate.Today().monthEof()) == 0) {
        // if (cal.get(Calendar.HOUR_OF_DAY) >= 23)
        // throw new
        // RuntimeException("系统现正在进行月初例行维护，维护时间为月底晚上23点至月初早上5点，请您在这段时间内不要使用系统，谢谢！");
        // }
        // 月初第一天
        // if (TDate.Today().compareTo(TDate.Today().monthBof()) == 0)
        // if (cal.get(Calendar.HOUR_OF_DAY) < 5)
        // throw new
        // RuntimeException("系统现正在进行月初例行维护，维护时间为月底晚上23点至月初早上5点，请您在这段时间内不要使用系统，谢谢！");
        return true;
    }

    // 是否在当前设备使用此菜单，如：检验此设备是否需要设备验证码
    protected boolean passDevice(IForm form) {
        // 若是iPhone应用商店测试或地藤体验账号则跳过验证
        if (isExperienceAccount(form)) {
            return true;
        }

        String deviceId = form.getClient().getId();
        // TODO 验证码变量，需要改成静态变量，统一取值
        String verifyCode = form.getRequest().getParameter("verifyCode");
        log.debug(String.format("进行设备认证, deviceId=%s", deviceId));
        String userId = (String) form.getHandle().getProperty(Application.userId);
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionInfo, userId, deviceId)) {
            if (!buff.isNull()) {
                if (buff.getBoolean("VerifyMachine")) {
                    log.debug("已经认证过，跳过认证");
                    return true;
                }
            }

            boolean result = false;
            LocalService app = new LocalService(form.getHandle());
            app.setService("SvrUserLogin.verifyMachine");
            app.getDataIn().getHead().setField("deviceId", deviceId);
            if (verifyCode != null && !"".equals(verifyCode))
                app.getDataIn().getHead().setField("verifyCode", verifyCode);

            if (app.exec())
                result = true;
            else {
                int used = app.getDataOut().getHead().getInt("Used_");
                if (used == 1)
                    result = true;
                else
                    form.setParam("message", app.getMessage());
            }
            if (result)
                buff.setField("VerifyMachine", true);
            return result;
        }
    }

    // 调用页面控制器指定的函数
    protected void callForm(IForm form, String funcCode) throws ServletException, IOException {
        HttpServletResponse response = form.getResponse();
        HttpServletRequest request = form.getRequest();
        if ("excel".equals(funcCode)) {
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=excel.csv");
        } else
            response.setContentType("text/html;charset=UTF-8");

        Object pageOutput = "";
        String sid = request.getParameter(RequestData.appSession_Key);
        if (sid == null || sid.equals(""))
            sid = request.getSession().getId();

        Method method = null;
        long startTime = System.currentTimeMillis();
        try {
            String CLIENTVER = request.getParameter("CLIENTVER");
            if (CLIENTVER != null)
                request.getSession().setAttribute("CLIENTVER", CLIENTVER);

            // 是否拥有此菜单调用权限
            if (!Application.getPassport(form.getHandle()).passForm(form)) {
                log.warn(String.format("无权限执行 %s", request.getRequestURL()));
                throw new RuntimeException("对不起，您没有权限执行此功能！");
            }

            // 专用测试账号则跳过设备认证的判断
            if (isExperienceAccount(form)) {
                try {
                    if (form.getClient().isPhone()) {
                        try {
                            method = form.getClass().getMethod(funcCode + "_phone");
                        } catch (NoSuchMethodException e) {
                            method = form.getClass().getMethod(funcCode);
                        }
                    } else
                        method = form.getClass().getMethod(funcCode);
                    pageOutput = method.invoke(form);
                } catch (PageException e) {
                    form.setParam("message", e.getMessage());
                    pageOutput = e.getViewFile();
                }
            } else {
                // 检验此设备是否需要设备验证码
                if (form.getHandle().getProperty(Application.userId) == null || form.passDevice() || passDevice(form)) {
                    try {
                        if (form.getClient().isPhone()) {
                            try {
                                method = form.getClass().getMethod(funcCode + "_phone");
                            } catch (NoSuchMethodException e) {
                                method = form.getClass().getMethod(funcCode);
                            }
                        } else {
                            method = form.getClass().getMethod(funcCode);
                        }
                        pageOutput = method.invoke(form);
                    } catch (PageException e) {
                        form.setParam("message", e.getMessage());
                        pageOutput = e.getViewFile();
                    }
                } else {
                    log.debug("没有进行认证过，跳转到设备认证页面");
                    pageOutput = new RedirectPage(form, Application.getAppConfig().getFormVerifyDevice());
                }
            }

            // 处理返回值
            if (pageOutput != null) {
                if (pageOutput instanceof IPage) {
                    IPage output = (IPage) pageOutput;
                    output.execute();
                } else {
                    log.warn(String.format("%s pageOutput is not IPage: %s", funcCode, pageOutput));
                    JspPage output = new JspPage(form);
                    output.setJspFile((String) pageOutput);
                    output.execute();
                }
            }
        } catch (Exception e) {
            Throwable err = e.getCause();
            if (err == null)
                err = e;
            ErrorPage opera = new ErrorPage(form, err);
            opera.execute();
        } finally {
            if (method != null) {
                long timeout = 1000;
                Webpage webpage = method.getAnnotation(Webpage.class);
                if (webpage != null)
                    timeout = webpage.timeout();
                checkTimeout(form, funcCode, startTime, timeout);
            }
        }
    }

    protected boolean isExperienceAccount(IForm form) {
        return getIphoneAppstoreAccount().equals(form.getHandle().getUserCode())
                || "16307405".equals(form.getHandle().getUserCode())
                || "15531101".equals(form.getHandle().getUserCode());
    }

    protected void checkTimeout(IForm form, String funcCode, long startTime, long timeout) {
        long totalTime = System.currentTimeMillis() - startTime;
        if (totalTime > timeout) {

            String tmp[] = form.getClass().getName().split("\\.");
            String pageCode = tmp[tmp.length - 1] + "." + funcCode;

            String dataIn = new Gson().toJson(form.getRequest().getParameterMap());
            if (dataIn.length() > 60000)
                dataIn = dataIn.substring(0, 60000);
            LocalService ser = new LocalService(form.getHandle(), "SvrFormTimeout.save");
            Record head = ser.getDataIn().getHead();
            head.setField("pageCode", pageCode);
            head.setField("dataIn", dataIn);
            head.setField("tickCount", totalTime);
            ser.exec();
        }
    }

    protected String getRequestCode(HttpServletRequest req) {
        String url = null;
        String args[] = req.getServletPath().split("/");
        if (args.length == 2 || args.length == 3) {
            if (args[0].equals("") && !args[1].equals("")) {
                if (args.length == 3)
                    url = args[2];
                else {
                    String sid = (String) req.getAttribute(RequestData.appSession_Key);
                    AppConfig conf = Application.getAppConfig();
                    if (sid != null && !"".equals(sid))
                        url = conf.getFormDefault();
                    else
                        url = conf.getFormWelcome();
                }
            }
        }
        return url;
    }

    // iphone 上架时专用测试帐号以及专业版体验账号
    protected String getIphoneAppstoreAccount() {
        return "15202406";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
