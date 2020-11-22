from thinkdsp import read_wave
# import pydub
# import ffmpeg
import librosa
import numpy as np


def find_nearest(value, array):
    array = np.asarray(array)
    idx = (np.abs(array - value)).argmin()
    return array[idx]

def process_frequencies(raw_frequencies):
    f = open('output.txt', 'a')
    columns = list(zip(*raw_frequencies))
    amps, freqs = columns[0], columns[1]

    # delete frequencies if amplitude <= k*MAX_AMP
    freq_indexes_to_delete = [i for i, n in enumerate(amps) if n < 0.1 * amps[0]]
    freqs = np.delete(freqs, freq_indexes_to_delete)

    # Frequencies processing
    midi_keys = np.arange(36, 109)
    midi_freqs = librosa.midi_to_hz(midi_keys)

    freqs = [round(librosa.hz_to_midi(find_nearest(f, midi_freqs))) for f in freqs if
             abs(f - find_nearest(f, midi_freqs)) < 4]

    f.write(str(freqs))
    f.write("\n")


# mp3_path = "Yiruma - River Flows in You.mp3"
# wav_path = "Sine440.wav"
wav_path = "Yiruma - River Flows in You.wav"

# TODO ТАК СКАЗАТЬ
## convert wav to mp3
# sound = AudioSegment.from_mp3(mp3_path)
# sound.export(wav_path, format="wav")

wave = read_wave(wav_path)
wave.normalize()
song_duration = wave.duration

# clear file before writing
open('output.txt', 'w').close()

start = 0
duration_frame = 0.25
while start <= song_duration - duration_frame:
    start += duration_frame
    segment = wave.segment(start, duration_frame)

    spectrum = segment.make_spectrum()
    spectrum.low_pass(4200)

    raw_frequencies = spectrum.peaks()[:15]
    process_frequencies(raw_frequencies)
