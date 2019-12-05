package com.iven.musicplayergo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.iven.musicplayergo.R
import com.iven.musicplayergo.music.Music
import com.iven.musicplayergo.music.MusicUtils
import com.iven.musicplayergo.player.MediaPlayerHolder
import com.iven.musicplayergo.ui.ThemeHelper
import com.iven.musicplayergo.ui.Utils

class QueueAdapter(
    private val context: Context,
    private val queueSongsDialog: MaterialDialog,
    private val mediaPlayerHolder: MediaPlayerHolder
) :
    RecyclerView.Adapter<QueueAdapter.QueueHolder>() {

    private var mQueueSongs = mediaPlayerHolder.queueSongs
    private var mSelectedSong = mediaPlayerHolder.currentSong

    private val mDefaultTextColor =
        ThemeHelper.resolveColorAttr(context, android.R.attr.textColorPrimary)

    fun swapSelectedSong(song: Music) {
        notifyItemChanged(mQueueSongs.indexOf(mSelectedSong.first))
        mSelectedSong = Pair(song, true)
        notifyItemChanged(mQueueSongs.indexOf(mSelectedSong.first))
    }

    fun swapQueueSongs(queueSongs: MutableList<Music>) {
        mQueueSongs = queueSongs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueHolder {
        return QueueHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.song_item_alt,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mQueueSongs.size
    }

    override fun onBindViewHolder(holder: QueueHolder, position: Int) {
        holder.bindItems(mQueueSongs[holder.adapterPosition])
    }

    inner class QueueHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(song: Music) {

            val title = itemView.findViewById<TextView>(R.id.title)
            val duration = itemView.findViewById<TextView>(R.id.duration)
            val subtitle = itemView.findViewById<TextView>(R.id.subtitle)

            title.text = song.title
            duration.text = MusicUtils.formatSongDuration(song.duration, false)
            subtitle.text =
                context.getString(R.string.artist_and_album, song.artist, song.album)

            val themeAccent = ThemeHelper.resolveThemeAccent(context)

            title.apply {
                when {
                    mQueueSongs.indexOf(mSelectedSong.first) == adapterPosition && mSelectedSong.second -> setTextColor(
                        themeAccent
                    )
                    else -> setTextColor(mDefaultTextColor)
                }
            }

            itemView.apply {
                setOnClickListener {

                    mediaPlayerHolder.apply {
                        when {
                            isSongRestoredFromPrefs -> isPlay = true
                            else -> if (!mediaPlayerHolder.isQueueStarted)
                                mediaPlayerHolder.setCurrentSong(
                                    song,
                                    queueSongs,
                                    true
                                ) else
                                currentSong = Pair(song, true)
                        }
                        initMediaPlayer(song)

                    }
                }

                setOnLongClickListener {
                    if (title.currentTextColor != ThemeHelper.resolveThemeAccent(context))
                        Utils.showDeleteQueueSongDialog(
                            context,
                            Pair(song, adapterPosition),
                            queueSongsDialog,
                            this@QueueAdapter,
                            mediaPlayerHolder
                        )

                    return@setOnLongClickListener true
                }
            }
        }
    }
}