import typing as tp
from thinkdsp import read_wave, Wave, Spectrum  # type: ignore
# import pydub
# import ffmpeg
import librosa  # type: ignore
import numpy as np  # type: ignore


def find_nearest(value: float, array: np.ndarray) -> float:
    """
    :param value:
    :param array:
    :return:
    """
    array = np.asarray(array)
    idx: np.ndarray = (np.abs(array - value)).argmin()
    return array[idx]


def process_frequencies(raw_frequencies: tp.List[tp.Tuple[tp.Any, ...]]) -> None:
    """
    :param raw_frequencies:
    :return:
    """
    with open('output.txt', 'a') as f:
        columns: tp.Iterator[tp.Tuple[tp.Any, ...]] = zip(*raw_frequencies)
        amps, frequencies = next(columns), next(columns)

        # delete frequencies if amplitude <= k*MAX_AMP
        freq_indexes_to_delete: tp.List[int] = \
            [i for i, n in enumerate(amps) if n < 0.1 * amps[0]]
        np_frequencies: np.ndarray = np.delete(frequencies, freq_indexes_to_delete)

        midi_keys: np.ndarray = np.arange(36, 109)
        midi_frequencies = librosa.midi_to_hz(midi_keys)

        result_frequencies: tp.List[int] = [
            round(librosa.hz_to_midi(find_nearest(f, midi_frequencies)))
            for f in np_frequencies
            if abs(f - find_nearest(f, midi_frequencies)) < 4
        ]
        print(str(result_frequencies), end='\n', file=f)


# mp3_path = "Yiruma - River Flows in You.mp3"
# wav_path = "Sine440.wav"


# TODO ТАК СКАЗАТЬ
# convert wav to mp3
# sound = AudioSegment.from_mp3(mp3_path)
# sound.export(wav_path, format="wav")

def main() -> None:
    wav_path: str = "Yiruma - River Flows in You.wav"
    wave: Wave = read_wave(wav_path)
    wave.normalize()
    song_duration: float = wave.duration

    # clear file before writing
    open('output.txt', 'w').close()

    start: float = 0
    duration_frame: float = 0.25
    while start <= song_duration - duration_frame:
        start += duration_frame
        segment: Wave = wave.segment(start, duration_frame)

        spectrum: Spectrum = segment.make_spectrum()
        spectrum.low_pass(4200)

        raw_frequencies: tp.List[tp.Tuple[tp.Any, ...]] = spectrum.peaks()[:15]
        process_frequencies(raw_frequencies)


if __name__ == "__main__":
    main()
