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
        mVimCmdCollections.append(VimCmd.BasicMovements.ordinal(), mRes.getStringArray(R.array.commands_array_basic));
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
