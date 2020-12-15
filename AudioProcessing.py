import dataclasses
import typing as tp

# import pydub
# import ffmpeg
import librosa  # type: ignore
import numpy as np  # type: ignore

from thinkdsp import read_wave, Wave, Spectrum  # type: ignore

MIDI_FREQUENCIES = librosa.midi_to_hz(np.arange(36, 109))


@dataclasses.dataclass
class Midi:
    midi_number: int
    start: float
    duration: float
    amplitude: float

    def __str__(self):
        return "(" + str(self.midi_number) + ", " + str(self.start) + ", " + str(self.duration) + ", " + str(
            self.amplitude) + ")"


def find_nearest(value: float) -> float:
    """
    :param value: value to find
    :return:
    """
    idx: int = (np.abs(MIDI_FREQUENCIES - value)).argmin()
    return MIDI_FREQUENCIES[idx]


def filter_low_amplitudes(amps_and_frequencies: tp.List[tp.Tuple[float, float]]) -> tp.List[tp.Tuple[float, float]]:
    """
    :param amps_and_frequencies: list of pairs (amplitude, frequency)
    :return: list of pairs (amplitude, frequency), where amplitude is not lower than 10% of maximal amplitude
    """
    if not amps_and_frequencies:
        return []
    max_amplitude: float = amps_and_frequencies[0][0]
    min_coefficient = 0.1
    result_list: tp.List[tp.Tuple[float, float]] = []
    for i, amp_and_frequency in enumerate(amps_and_frequencies):
        if amp_and_frequency[0] > min_coefficient * max_amplitude:
            result_list.append(amp_and_frequency)
    return result_list


def get_midi_from_frequencies(amps_and_frequencies: tp.List[tp.Tuple[float, float]]) -> tp.List[tp.Tuple[int, float]]:
    """
    :param amps_and_frequencies: list of pairs (amplitude, frequency)
    :return: list of pairs (midi, amplitude) sorted by midi value
    """
    midi_and_amps: tp.List[tp.Tuple[int, float]] = []
    for amp_and_frequency in amps_and_frequencies:
        nearest: float = find_nearest(amp_and_frequency[1])
        if abs(amp_and_frequency[1] - nearest) < 4:
            midi_and_amps.append((round(librosa.hz_to_midi(nearest)), amp_and_frequency[0]))
    return sorted(midi_and_amps)


def reduce_duplicates(midi_and_amplitude: tp.List[tp.Tuple[int, float]]) -> tp.List[tp.Tuple[int, float]]:
    if not midi_and_amplitude:
        return []
    reduced: tp.List[tp.Tuple[int, float]] = []
    current: int = midi_and_amplitude[0][0]
    current_amplitude: float = midi_and_amplitude[0][1]
    for i in range(1, len(midi_and_amplitude)):
        if midi_and_amplitude[i][0] == current:
            current_amplitude += midi_and_amplitude[i][1]
        else:
            reduced.append((current, current_amplitude))
            current = midi_and_amplitude[i][0]
    reduced.append((current, current_amplitude))
    return reduced


def process_frequencies(raw_frequencies: tp.List[tp.Tuple[float, float]]) -> tp.List[tp.Tuple[int, float]]:
    """
    :param raw_frequencies: list of pairs (amplitude, frequency)
    :return: list of pairs (midi number, amplitude)
    """
    amps_and_frequencies: tp.List[tp.Tuple[float, float]] = filter_low_amplitudes(raw_frequencies)
    return reduce_duplicates(get_midi_from_frequencies(amps_and_frequencies))


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

    start: float = 0
    duration_frame: float = 0.25
    midis_and_amplitudes: tp.List[tp.List[tp.Tuple[int, float]]] = []
    # with open('output.txt', 'w') as f:
    while start <= song_duration - duration_frame:
        segment: Wave = wave.segment(start, duration_frame)

        spectrum: Spectrum = segment.make_spectrum()
        spectrum.low_pass(4200)

        raw_frequencies: tp.List[tp.Tuple[float, float]] = spectrum.peaks()[:15]
        midis_and_amplitudes.append(process_frequencies(raw_frequencies))
        start += duration_frame


if __name__ == "__main__":
    main()
