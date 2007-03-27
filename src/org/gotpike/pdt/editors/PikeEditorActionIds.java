package org.gotpike.pdt.editors;

/**
 * Constants identifying actions contributed by the org.gotpike.pdt plug-in. These action ids
 * are shared by the plug-in manifest, PerlActionContributor, PerlEditor and PerlEditorAction.
 *
 *@author jploski
 */
public class PikeEditorActionIds
{
    //~ Static fields/initializers

    /** org.gotpike.pdt.actions.ClearMarkerAction#All */
    public static final String CLEAR_ALL_MARKERS =
        "org.gotpike.pdt.actions.ClearMarkerAction#AllMarkers";

    /** org.gotpike.pdt.actions.ClearMarkerAction#PodChecker */
    public static final String CLEAR_POD_MARKERS =
        "org.gotpike.pdt.actions.ClearMarkerAction#PodChecker";

    /** org.gotpike.pdt.actions.ClearMarkerAction#Critic */
    public static final String CLEAR_CRITIC_MARKERS =
        "org.gotpike.pdt.actions.ClearMarkerAction#Critic";

    /** org.gotpike.pdt.actions.PodCheckerAction */
    public static final String POD_CHECKER = "org.gotpike.pdt.actions.PodCheckerAction";

    /** org.gotpike.pdt.actions.PerlCriticAction */
    public static final String PERL_CRITIC = "org.gotpike.pdt.actions.PerlCriticAction";

    /** org.gotpike.pdt.actions.FormatSourceAction */
    public static final String FORMAT_SOURCE = "org.gotpike.pdt.actions.FormatSourceAction";

    /** org.gotpike.pdt.actions.HtmlExportAction */
    public static final String HTML_EXPORT = "org.gotpike.pdt.actions.ExportHtmlAction";

    /** org.gotpike.pdt.actions.ContentAssistAction */
    public static final String CONTENT_ASSIST = "org.gotpike.pdt.actions.ContentAssistAction";

    /** org.gotpike.pdt.actions.ValidateSyntaxAction */
    public static final String VALIDATE_SYNTAX = "org.gotpike.pdt.actions.ValidateSyntaxAction";

    /** org.gotpike.pdt.actions.ToggleCommentAction */
    public static final String TOGGLE_COMMENT = "org.gotpike.pdt.actions.ToggleCommentAction";

    /** org.gotpike.pdt.actions.OpenDeclarationAction */
    public static final String OPEN_DECLARATION =
        "org.gotpike.pdt.actions.OpenDeclarationAction";

    /** org.gotpike.pdt.actions.PerlDocAction */
    public static final String PERL_DOC = "org.gotpike.pdt.actions.PerlDocAction";

    /** org.gotpike.pdt.actions.Jump2BracketAction */
    public static final String MATCHING_BRACKET = "org.gotpike.pdt.actions.Jump2BracketAction";

    /** org.gotpike.pdt.actions.ToggleMarkOccurrencesAction */
    public static final String TOGGLE_MARK_OCCURRENCES =
        "org.gotpike.pdt.actions.ToggleMarkOccurrencesAction";

    /** org.gotpike.pdt.commands.extractSubroutine */
    public static final String EXTRACT_SUBROUTINE =
        "org.gotpike.pdt.actions.ExtractSubroutineAction";

    //~ Constructors

    private PikeEditorActionIds()
    {
        // empty impl
    }

    //~ Methods

    /**
     * @return Ids of all {@link org.gotpike.pdt.actions.PerlEditorAction PerlEditorAction}s
     *         that are owned by the
     *         {@link org.gotpike.pdt.editors.PerlActionContributor PerlActionContributor}
     */
    public static final String[] getContributorActions()
    {
        return new String[] { TOGGLE_MARK_OCCURRENCES };
    }

    /**
     * @return Ids of all {@link org.gotpike.pdt.actions.PerlEditorAction}s that are owned by
     *         the {@link org.gotpike.pdt.editors.PerlEditor PerlEditor}
     */
    public static final String[] getEditorActions()
    {
        return new String[]
            {
                CLEAR_ALL_MARKERS,
                CLEAR_POD_MARKERS,
                CLEAR_CRITIC_MARKERS,
                POD_CHECKER,
                CONTENT_ASSIST,
                PERL_CRITIC,
                EXTRACT_SUBROUTINE,
                FORMAT_SOURCE,
                HTML_EXPORT,
                MATCHING_BRACKET,
                OPEN_DECLARATION,
                PERL_DOC,
                TOGGLE_COMMENT,
                VALIDATE_SYNTAX
            };
    }
}
