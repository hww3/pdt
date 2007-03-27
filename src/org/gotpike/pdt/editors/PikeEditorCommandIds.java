package org.gotpike.pdt.editors;

import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;


/**
 * Constants identifying commands contributed by the org.gotpike.pdt plug-in. These command ids
 * (aka ActionDefinitionIds) are used by the PerlEditor.
 *
 * <p>The overall idea is that both user-configurable keyboard shortcuts and plug-in actions are
 * associated with command ids, creating the opportunity of uniform keyboard shortcuts across
 * different plug-ins. However, EPIC's actions are quite specific and thus currently do not match
 * many workbench commands. Instead, EPIC provides its own set of commands, at risk of having the
 * user configure the same keyboard shortcuts twice (e.g. for JDT and EPIC).</p>
 *
 * @author jploski
 */
public class PikeEditorCommandIds
{
    //~ Static fields/initializers

    /** org.gotpike.pdt.commands.clearMarker */
    public static final String CLEAR_MARKER = "org.gotpike.pdt.commands.clearMarker";

    /** org.gotpike.pdt.commands.podChecker */
    public static final String POD_CHECKER = "org.gotpike.pdt.commands.podChecker";

    /** org.gotpike.pdt.commands.perlCritic */
    public static final String CRITIQUE_SOURCE = "org.gotpike.pdt.commands.perlCritic";

    /** org.gotpike.pdt.commands.formatSource */
    public static final String FORMAT_SOURCE = "org.gotpike.pdt.commands.formatSource";

    /**
     * @see ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS
     */
    public static final String CONTENT_ASSIST =
        ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;

    /** org.gotpike.pdt.commands.htmlExport */
    public static final String HTML_EXPORT = "org.gotpike.pdt.commands.htmlExport";

    /** org.gotpike.pdt.commands.validateSyntax */
    public static final String VALIDATE_SYNTAX = "org.gotpike.pdt.commands.validateSyntax";

    /** org.gotpike.pdt.commands.toggleComment */
    public static final String TOGGLE_COMMENT = "org.gotpike.pdt.commands.toggleComment";

    /** org.gotpike.pdt.commands.openDeclaration */
    public static final String OPEN_DECLARATION = "org.gotpike.pdt.commands.openDeclaration";

    /** org.gotpike.pdt.commands.searchPerlDoc */
    public static final String PERL_DOC = "org.gotpike.pdt.commands.searchPerlDoc";

    /** org.gotpike.pdt.jump2Bracket */
    public static final String MATCHING_BRACKET = "org.gotpike.pdt.commands.jump2Bracket";

    /** org.gotpike.pdt.commands.toggleMarkOccurrences */
    public static final String TOGGLE_MARK_OCCURRENCES =
        "org.gotpike.pdt.commands.toggleMarkOccurrences";

    /** org.gotpike.pdt.commands.extractSubroutine */
    public static final String EXTRACT_SUBROUTINE =
        "org.gotpike.pdt.commands.extractSubroutine";

    //~ Constructors

    private PikeEditorCommandIds()
    {
    }
}
