package org.gotpike.pdt.util;

import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.preferences.PreferenceConstants;


public class PreferenceUtil {

	/**
	 * @return
	 */
	public static String getTab(int column) {
		boolean useSpaces =
			PDTPlugin.getDefault().getPreferenceStore().getBoolean(
				PreferenceConstants.SPACES_INSTEAD_OF_TABS);


		String tabString = null;

		if (useSpaces) {
			int numSpaces =
				PDTPlugin.getDefault().getPreferenceStore().getInt(
				PreferenceConstants.INSERT_TABS_ON_INDENT);
            
            if (numSpaces > 0)
            {
			    char[] indentChars = new char[numSpaces - (column % numSpaces)];
			    for (int i = 0; i < indentChars.length; i++) {
				    indentChars[i] = ' ';
			    }
			    tabString = String.valueOf(indentChars);
            }
            else tabString = "";
		} else {
			tabString = "\t";
		}

		return tabString;
	}
}