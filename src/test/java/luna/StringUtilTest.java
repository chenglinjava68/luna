package luna;

import luna.util.StringUtil;

public class StringUtilTest{
    public static void main(String [] args){
        System.out.println(StringUtil.stripEscape(StringUtil.stripControl("ssewew223344\\u000essseegggffggr&*(&*……￥%##&……&%…………rwwweerr")));
        System.out.println(StringUtil.stripControl(StringUtil.stripEscape("ssewew223344\u000essseegggffggr&*(&*……￥%##&……&%…………rwwweerr")));
        System.out.println("\ufffe");
    }
}
