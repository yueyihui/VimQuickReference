package com.github.lyuecszhang.vimquickreference;

import android.content.res.Resources;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yue_liang on 16-12-7.
 */

/**
 * Use loadVimCmds firstly to load resource from the application.
 */
public class VimCmdCollections {
    private static SparseArray<String[]> mVimCmdCollections = new SparseArray<>();
    private static Resources mRes;

    public static String CURRENT_POSITION =
            "com.github.lyuecszhang.vimquickreference.current_position";

    private enum VimCmd {
        BasicMovements,
        InsertionReplace,
        Deletion,
        InsertMode,
        Copying,
        AdvancedInsertion,
        VisualMode,
        UndoingRepeatingRegisters,
        ComplexMovement,
        SearchSubstitute,
        SpecCharInSearchPatterns,
        OffsetIinSearchCmds,
        MarksAndMotions,
        KeyMappabbreviations,
        Tags,
        ScrollingMultiWindow,
        ExCmds,
        ExRanges,
        Folding,
        Miscellaneous
    }

    public static void loadVimCmds(Resources resources) {
        mRes = resources;
        mVimCmdCollections.append(VimCmd.BasicMovements.ordinal(),
                mRes.getStringArray(R.array.commands_array_basic));
        mVimCmdCollections.append(VimCmd.InsertionReplace.ordinal(),
                mRes.getStringArray(R.array.commands_array_insertion));
        mVimCmdCollections.append(VimCmd.Deletion.ordinal(),
                mRes.getStringArray(R.array.commands_array_deletion));
        mVimCmdCollections.append(VimCmd.InsertMode.ordinal(),
                mRes.getStringArray(R.array.commands_array_insert_mode));
        mVimCmdCollections.append(VimCmd.Copying.ordinal(),
                mRes.getStringArray(R.array.commands_array_deletion));
        mVimCmdCollections.append(VimCmd.AdvancedInsertion.ordinal(),
                mRes.getStringArray(R.array.commands_array_advanced_insertion));
        mVimCmdCollections.append(VimCmd.VisualMode.ordinal(),
                mRes.getStringArray(R.array.commands_array_visual_mode));
        mVimCmdCollections.append(VimCmd.UndoingRepeatingRegisters.ordinal(),
                mRes.getStringArray(R.array.commands_array_undoing));
        mVimCmdCollections.append(VimCmd.ComplexMovement.ordinal(),
                mRes.getStringArray(R.array.commands_array_complex_movement));
        mVimCmdCollections.append(VimCmd.SearchSubstitute.ordinal(),
                mRes.getStringArray(R.array.commands_array_search_sub));
        mVimCmdCollections.append(VimCmd.SpecCharInSearchPatterns.ordinal(),
                mRes.getStringArray(R.array.commands_array_special_char_search));
        mVimCmdCollections.append(VimCmd.OffsetIinSearchCmds.ordinal(),
                mRes.getStringArray(R.array.commands_array_offsets_search));
        mVimCmdCollections.append(VimCmd.MarksAndMotions.ordinal(),
                mRes.getStringArray(R.array.commands_array_marks_motions));
        mVimCmdCollections.append(VimCmd.KeyMappabbreviations.ordinal(),
                mRes.getStringArray(R.array.commands_array_key_mapping));
        mVimCmdCollections.append(VimCmd.Tags.ordinal(),
                mRes.getStringArray(R.array.commands_array_tags));
        mVimCmdCollections.append(VimCmd.ScrollingMultiWindow.ordinal(),
                mRes.getStringArray(R.array.commands_array_scrolling));
        mVimCmdCollections.append(VimCmd.ExCmds.ordinal(),
                mRes.getStringArray(R.array.commands_array_ex_commands));
        mVimCmdCollections.append(VimCmd.ExRanges.ordinal(),
                mRes.getStringArray(R.array.commands_array_ex_ranges));
        mVimCmdCollections.append(VimCmd.Folding.ordinal(),
                mRes.getStringArray(R.array.commands_array_folding));
        mVimCmdCollections.append(VimCmd.Miscellaneous.ordinal(),
                mRes.getStringArray(R.array.commands_array_misc));
    }

    public static String[] getVimCmdByPosition(int position) {
        return mVimCmdCollections.get(position);
    }

    public static String[] getVimTitle() {
        return mRes.getStringArray(R.array.header_array);
    }

    public static String[] getVimCmdBasicMovements() {
        return mVimCmdCollections.get(VimCmd.BasicMovements.ordinal());
    }
}
