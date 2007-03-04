package org.gotpike.pdt.util;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.gotpike.pdt.PikeCore;
import org.gotpike.pdt.PikeProject;
import org.gotpike.pdt.PDTPlugin;

/**
 * Responsible for the basic command lines used to invoke the Perl interpreter.
 * 
 * @author luelljoc
 * @author jploski
 */
public class PikeExecutableUtilities
{
    private static final Pattern CYGWIN_PATH_TRANSLATION =
        Pattern.compile("^([a-z]):(.*)$"); 
    
    private PikeExecutableUtilities() { }
    
    /**
     * @return a list of Strings representing the command line used to invoke
     *         the Perl interpreter, according to EPIC's global preferences;
     *         if EPIC is set up properly, the returned list should at
     *         the very least contain a path to the interpreter's executable 
     */
    public static List getPikeCommandLine()
    {
        return new ArrayList(CommandLineTokenizer.tokenize(
            PDTPlugin.getDefault().getExecutablePreference()));
    }

    /**
     * @param textEditor    editor for a resource within a Perl project
     * @return a list of Strings representing the command line used to invoke
     *         the Perl for scripts belonging to the enclosing project 
     */
    public static List getPikeCommandLine(ITextEditor textEditor)
    {
        IProject project = 
            ((IFileEditorInput) textEditor.getEditorInput())
            .getFile().getProject();

        return getPikeCommandLine(PikeCore.create(project));
    }
    
    /**
     * @param project       a Perl project
     * @return a list of Strings representing the command line used to invoke
     *         the Perl interpreter for scripts belonging to this project.
     *         This equals the command line returned by
     *         {@link #getPerlCommandLine()} with the project-specific
     *         include path appended to it. 
     */
    public static List getPikeCommandLine(PikeProject project)
    {
        List commandLine = getPikeCommandLine();        
        commandLine.addAll(getPikeIncArgs(project));
        return commandLine;
    }

    /**
     * @param project   a Perl project
     * @return a list of command-line parameters (Strings) representing
     *         the project's include path; the directories are
     *         translated for Cygwin if necessary
     */
    public static List getPikeIncArgs(PikeProject project)
    {    
        List args = new ArrayList();
        
        for (Iterator i = project.getIncPath().iterator(); i.hasNext();)
        {
            String path = ((File) i.next()).getAbsolutePath();
            // replace '\\' by '/' due to problems with Brazil
            path = path.replace('\\', '/');
            args.add("-I" + path);
        }
        return args;
    }

    /**
     * @return path to the Perl interpreter's executable,
     *         according to EPIC's global preferences,
     *         or null if no path has been configured yet
     */
    public static String getPikeInterpreterPath()
    {
        List commandLine = getPikeCommandLine();
        if (commandLine.isEmpty()) return null;
        return (String) commandLine.get(0).toString().replace('\\', '/');
    }
    
    
    /**
     * @param absolute path to some directory,
     *        as returned by File.getAbsolutePath 
     * @return the same path normalized to / as separators and
     *         translated for Cygwin, if necessary
     */
    public static String resolveIncPath(String path)
    {
        path = path.replace('\\', '/');
        return path;
    }
    
}