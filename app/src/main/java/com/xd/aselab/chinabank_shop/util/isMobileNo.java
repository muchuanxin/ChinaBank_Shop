
package com.xd.aselab.chinabank_shop.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dorise on 2017/10/10.
 */

public class isMobileNo {
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
