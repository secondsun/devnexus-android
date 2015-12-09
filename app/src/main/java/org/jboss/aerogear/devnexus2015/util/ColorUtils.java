package org.jboss.aerogear.devnexus2015.util;

import android.content.Context;
import android.graphics.Color;

import org.jboss.aerogear.devnexus2015.R;

/**
 * Created by summers on 2/22/15.
 */
public class ColorUtils {
    public static int getTextColor(Context context, int color) {
        double lightness = lightness(color); 

        if (lightness >.5d) {
            return (context.getResources().getColor(R.color.dn_white));
        } else {
            return (context.getResources().getColor(R.color.dn_white));
        }
    }

    public static int getDrawerDrawable(int color) {
        double lightness = lightness(color); 

        if (lightness >.5d) {
            return R.drawable.ic_drawer_black;
        } else {
            return R.drawable.ic_drawer_white;
        }
    }

    private static double lightness(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Math.sqrt(r*r*.299 + g*g*.587 + b*b*.114)/255d;//Calculate luminance
    }

}
