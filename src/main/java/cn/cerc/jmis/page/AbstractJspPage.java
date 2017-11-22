package cn.cerc.jmis.page;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.form.IForm;
import cn.cerc.jbean.form.IPage;
import cn.cerc.jbean.other.MemoryBuffer;
import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.TDate;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.other.utils;
import cn.cerc.jmis.tools.R;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlContent;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.core.UrlRecord;
import cn.cerc.jpage.other.UrlMenu;
import cn.cerc.jui.parts.MainMenu;
import cn.cerc.jui.parts.UIComponent;
import cn.cerc.jui.parts.UIDocument;
import cn.cerc.jui.parts.UIFooter;
import cn.cerc.jui.parts.UIHeader;
import cn.cerc.jui.parts.UIToolBar;

public abstract class AbstractJspPage extends Component implements IPage {
    private String jspFile;
    private IForm form;
    private List<String> styleFiles = new ArrayList<>();
    private List<String> scriptFiles = new ArrayList<>();
    private List<HtmlContent> scriptCodes = new ArrayList<>();
    // 头部：广告+菜单
    protected UIHeader header;
    // 主体: 控制区(可选)+内容+消息区
    private UIDocument document;
    // 工具面板：多页形式
    private UIToolBar toolBar;
    // 状态栏：快捷操作+按钮组
    private UIFooter footer;

    public AbstractJspPage(IForm form) {
        super();
        this.setForm(form);
    }

    @Override
    public final void setForm(IForm form) {
        this.form = form;
        if (form != null)
            this.put("jspPage", this);
    }

    @Override
    public final IForm getForm() {
        return form;
    }

    @Override
    public void addComponent(Component component) {
        if (component.getId() != null)
            this.put(component.getId(), component);
        super.addComponent(component);
    }

    @Override
    public void execute() throws ServletException, IOException {
        String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(), this.getViewFile());
        getRequest().getServletContext().getRequestDispatcher(url).forward(getRequest(), getResponse());
    }

    public final String getJspFile() {
        return jspFile;
    }

    public final void setJspFile(String jspFile) {
        this.jspFile = jspFile;
    }

    protected void put(String id, Object value) {
        getRequest().setAttribute(id, value);
    }

    public final String getMessage() {
        return form.getParam("message", null);
    }

    public final void setMessage(String message) {
        form.setParam("message", message);
    }

    public final String getViewFile() {
        String jspFile = this.getJspFile();
        if (getRequest() == null || jspFile == null)
            return jspFile;
        if (jspFile.indexOf(".jsp") == -1)
            return jspFile;

        String rootPath = String.format("/WEB-INF/%s/", Application.getAppConfig().getPathForms());
        String fileName = jspFile.substring(0, jspFile.indexOf(".jsp"));
        String extName = jspFile.substring(jspFile.indexOf(".jsp") + 1);

        // 检查是否存在 PC 专用版本的jsp文件
        String newFile = String.format("%s-%s.%s", fileName, "pc", extName);
        if (!this.getForm().getClient().isPhone() && fileExists(rootPath + newFile)) {
            // 检查是否存在相对应的语言版本
            String langCode = form == null ? Application.LangageDefault : R.getLanguage(form.getHandle());
            String langFile = String.format("%s-%s-%s.%s", fileName, "pc", langCode, extName);
            if (fileExists(rootPath + langFile))
                return langFile;
            return newFile;
        }

        // 检查是否存在相对应的语言版本
        String langCode = form == null ? Application.LangageDefault : R.getLanguage(form.getHandle());
        String langFile = String.format("%s-%s.%s", fileName, langCode, extName);
        if (fileExists(rootPath + langFile))
            return langFile;

        return jspFile;
    }

    protected boolean fileExists(String fileName) {
        URL url = AbstractJspPage.class.getClassLoader().getResource("");
        if (url == null)
            return false;
        String filepath = url.getPath();
        String appPath = filepath.substring(0, filepath.indexOf("/WEB-INF"));
        String file = appPath + fileName;
        File f = new File(file);
        return f.exists();
    }

    // 从请求或缓存读取数据
    public final String getValue(MemoryBuffer buff, String reqKey) {
        String result = getRequest().getParameter(reqKey);
        if (result == null) {
            String val = buff.getString(reqKey).replace("{}", "");
            if (utils.isNumeric(val) && val.endsWith(".0"))
                result = val.substring(0, val.length() - 2);
            else
                result = val;
        } else {
            result = result.trim();
            buff.setField(reqKey, result);
        }
        this.add(reqKey, result);
        return result;
    }

    public final List<String> getStyleFiles() {
        return styleFiles;
    }

    public final List<String> getScriptFiles() {
        return scriptFiles;
    }

    public final List<HtmlContent> getScriptCodes() {
        return scriptCodes;
    }

    public final void addStyleFile(String file) {
        styleFiles.add(file);
    }

    public final void addScriptFile(String scriptFile) {
        scriptFiles.add(scriptFile);
    }

    public final void addScriptCode(HtmlContent scriptCode) {
        scriptCodes.add(scriptCode);
    }

    // 返回所有的样式定义，供jsp中使用 ${jspPage.css}调用
    public final HtmlWriter getCss() {
        HtmlWriter html = new HtmlWriter();
        for (String file : styleFiles)
            html.println("<link href=\"%s\" rel=\"stylesheet\">", file);
        return html;
    }

    // 返回所有的脚本，供jsp中使用 ${jspPage.script}调用
    public final HtmlWriter getScript() {
        HtmlWriter html = new HtmlWriter();

        // 加入脚本文件
        for (String file : getScriptFiles()) {
            html.println("<script src=\"%s\"></script>", file);
        }
        // 加入脚本代码
        if (scriptCodes.size() > 0) {
            html.println("<script>");
            if (scriptCodes.size() > 0) {
                html.println("$(function(){");
                for (HtmlContent func : scriptCodes) {
                    func.output(html);
                }
                html.println("});");
            }
            html.println("</script>");
        }
        return html;
    }

    public void add(String id, String value) {
        getRequest().setAttribute(id, value);
    }

    public void add(String id, boolean value) {
        put(id, value);
    }

    public void add(String id, double value) {
        put(id, value);
    }

    public void add(String id, List<?> value) {
        put(id, value);
    }

    public void add(String id, Map<?, ?> value) {
        put(id, value);
    }

    public void add(String id, DataSet value) {
        put(id, value);
    }

    public void add(String id, Record value) {
        put(id, value);
    }

    public void add(String id, TDate value) {
        put(id, value);
    }

    public void add(String id, TDateTime value) {
        put(id, value);
    }

    public void add(String id, UIComponent value) {
        put(id, value);
    }

    public UIFooter getFooter() {
        if (footer == null) {
            footer = new UIFooter(this);
            footer.setId("bottom");
            this.put("bottom", footer);
        }
        return footer;
    }

    public MainMenu getMainMenu() {
        return getHeader().getMainMenu();
    }

    public UIHeader getHeader() {
        if (header == null) {
            header = new UIHeader(this);
        }
        return header;
    }

    public UIDocument getDocument() {
        if (document == null) {
            document = new UIDocument(this);
        }
        return document;
    }

    public UIToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new UIToolBar(this);
        }
        return toolBar;
    }

    protected final void initHeader() {
        HttpServletRequest request = this.getRequest();
        Component left = header.getLeft();
        @SuppressWarnings("unchecked")
        List<UrlRecord> barMenus = (List<UrlRecord>) request.getAttribute("barMenus");
        if (barMenus == null) {
            new UrlMenu(left, "首页", "/");
            new UrlMenu(left, "刷新", "javascript:history.go(-1);");
        } else {
            for (UrlRecord menu : barMenus) {
                new UrlMenu(left, menu.getName(), menu.getUrl());
            }
        }

        Component right = header.getRight();
        @SuppressWarnings("unchecked")
        List<UrlRecord> subMenus = (List<UrlRecord>) request.getAttribute("subMenus");
        if (subMenus != null) {
            int i = subMenus.size() - 1;
            while (i > -1) {
                UrlRecord menu = subMenus.get(i);
                new UrlMenu(right, menu.getName(), menu.getUrl());
                i--;
            }
        }
    }

}
