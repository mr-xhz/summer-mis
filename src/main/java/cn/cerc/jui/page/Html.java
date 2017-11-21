package cn.cerc.jui.page;

import java.io.PrintWriter;

public class Html {

    public Html(PrintWriter out, String falg, HtmlOut context) {
        out.print(String.format("<%s>", falg));
        out.print(context.toString());
        out.print(String.format("</%s>", falg));
    }

}
