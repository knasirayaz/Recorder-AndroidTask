package com.knasirayaz.recorder.data.models


data class Podcast (val filePath : String,val name : String, val duration: String,val recordedTime: String, var isPlaying : Boolean) : Comparable<Podcast>{
    override fun compareTo(other: Podcast): Int {
        return this.duration.compareTo(other.duration)
    }

}