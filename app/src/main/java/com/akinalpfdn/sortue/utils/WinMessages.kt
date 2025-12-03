package com.akinalpfdn.sortue.utils

import android.content.Context
import com.akinalpfdn.sortue.R

object WinMessages {
    fun getTitles(context: Context): List<String> {
        return listOf(
            context.getString(R.string.win_title_divine),
            context.getString(R.string.win_title_exquisite),
            context.getString(R.string.win_title_radiant),
            context.getString(R.string.win_title_sublime),
            context.getString(R.string.win_title_flawless),
            context.getString(R.string.win_title_brilliant),
            context.getString(R.string.win_title_zen),
            context.getString(R.string.win_title_perfect),
            context.getString(R.string.win_title_harmony),
            context.getString(R.string.win_title_masterpiece),
            context.getString(R.string.win_title_serene),
            context.getString(R.string.win_title_complete),
            context.getString(R.string.win_title_elegant),
            context.getString(R.string.win_title_sorted),
            context.getString(R.string.win_title_pure),
            context.getString(R.string.win_title_beautiful),
            context.getString(R.string.win_title_serene), // Duplicate
            context.getString(R.string.win_title_perfect), // Duplicate
            context.getString(R.string.win_title_sublime), // Duplicate
            context.getString(R.string.win_title_radiant), // Duplicate
            context.getString(R.string.win_title_tranquil),
            context.getString(R.string.win_title_lovely),
            context.getString(R.string.win_title_splendid),
            context.getString(R.string.win_title_graceful),
            context.getString(R.string.win_title_harmonious),
            context.getString(R.string.win_title_excellent),
            context.getString(R.string.win_title_wonderful),
            context.getString(R.string.win_title_calming),
            context.getString(R.string.win_title_peaceful),
            context.getString(R.string.win_title_brilliant), // Duplicate
            context.getString(R.string.win_title_flowing),
            context.getString(R.string.win_title_gentle),
            context.getString(R.string.win_title_smooth),
            context.getString(R.string.win_title_balanced),
            context.getString(R.string.win_title_aligned)
        )
    }

    fun getSubtitles(context: Context): List<String> {
        return listOf(
            context.getString(R.string.win_subtitle_spectrum),
            context.getString(R.string.win_subtitle_balance),
            context.getString(R.string.win_subtitle_vision),
            context.getString(R.string.win_subtitle_colors),
            context.getString(R.string.win_subtitle_chaos),
            context.getString(R.string.win_subtitle_satisfying),
            context.getString(R.string.win_subtitle_peaceful),
            context.getString(R.string.win_subtitle_clarity),
            context.getString(R.string.win_subtitle_smooth),
            context.getString(R.string.win_subtitle_eye),
            context.getString(R.string.win_subtitle_gradient),
            context.getString(R.string.win_subtitle_flow),
            context.getString(R.string.win_subtitle_tranquility),
            context.getString(R.string.win_subtitle_organized),
            context.getString(R.string.win_subtitle_rhythm),
            context.getString(R.string.win_subtitle_harmony_restored),
            context.getString(R.string.win_subtitle_peace),
            context.getString(R.string.win_subtitle_sync),
            context.getString(R.string.win_subtitle_order),
            context.getString(R.string.win_subtitle_pure_satisfaction),
            context.getString(R.string.win_subtitle_simply_delightful),
            context.getString(R.string.win_subtitle_gentle_success),
            context.getString(R.string.win_subtitle_balance_achieved),
            context.getString(R.string.win_subtitle_smooth_perfection),
            context.getString(R.string.win_subtitle_calm_clear),
            context.getString(R.string.win_subtitle_spectrum_flows),
            context.getString(R.string.win_subtitle_relax_breathe),
            context.getString(R.string.win_subtitle_well_sorted),
            context.getString(R.string.win_subtitle_perfect_gradient),
            context.getString(R.string.win_subtitle_zen_achieved),
            context.getString(R.string.win_subtitle_flow_state_found),
            context.getString(R.string.win_subtitle_nice_tidy),
            context.getString(R.string.win_subtitle_softly_aligned),
            context.getString(R.string.win_subtitle_vibrant_peace),
            context.getString(R.string.win_subtitle_quietly_perfect)
        )
    }
}
