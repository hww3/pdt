package org.gotpike.pdt.util;

// courtesy of the sunshade project

public class StringUtil
{
    public static boolean isEmpty(String val)
    {
        return val == null || val.length() == 0;
    }

    public static boolean isBlank(String val)
    {
        return val == null || val.trim().length() == 0;
    }
}
