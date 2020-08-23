from __future__ import unicode_literals
import youtube_dl
import os
from sys import argv

download_options = {
	'format': 'bestaudio/best',
        'quiet': True,
	'outtmpl': 'downloadedVideos\%(title)s.%(ext)s',
	'nocheckcertificate': True,
	'postprocessors': [{
		'key': 'FFmpegExtractAudio',
		'preferredcodec': 'mp3',
		'preferredquality': '192',
	}],
}

with youtube_dl.YoutubeDL(download_options) as dl:
    with open("youtube_video_urls2.txt", 'r') as f:
        for song_url in f:
            dl.download([song_url])

