import dataclasses
import typing as tp

# import pydub
# import ffmpeg
import librosa  # type: ignore
import numpy as np  # type: ignore

from thinkdsp import read_wave, Wave, Spectrum  # type: ignore

MIDI_FREQUENCIES = librosa.midi_to_hz(np.arange(36, 109))


@dataclasses.dataclass
class Note:
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

def get_midis_with_amplitudes(wav_path: str, duration_frame: float) -> tp.List[tp.List[tp.Tuple[int, float]]]:
    wave: Wave = read_wave(wav_path)
    wave.normalize()
    song_duration: float = wave.duration

    start = 0.0
    midis_and_amplitudes: tp.List[tp.List[tp.Tuple[int, float]]] = []
    # with open('output.txt', 'w') as f:
    while start <= song_duration - duration_frame:
        segment: Wave = wave.segment(start, duration_frame)

        spectrum: Spectrum = segment.make_spectrum()
        spectrum.low_pass(4200)

        raw_frequencies: tp.List[tp.Tuple[float, float]] = spectrum.peaks()[:15]
        midis_and_amplitudes.append(process_frequencies(raw_frequencies))
        start += duration_frame
    return midis_and_amplitudes


def get_dicts_from_lists(list_of_lists: tp.List[tp.List[tp.Tuple[int, float]]]) -> tp.List[tp.Dict[int, float]]:
    result_list: tp.List[tp.Dict[int, float]] = []
    for list_of_tuples in list_of_lists:
        inner_dict: tp.Dict[int, float] = {}
        for pair in list_of_tuples:
            inner_dict[pair[0]] = pair[1]
        result_list.append(inner_dict)
    return result_list


def get_notes(midi_dicts_list: tp.List[tp.Dict[int, float]], duration_frame: float) -> tp.List[Note]:
    result_notes: tp.List[Note] = []
    minimal_difference = 15.0
    for i, midi_dict in enumerate(midi_dicts_list):
        for midi, amplitude in midi_dict.items():
            amplitudes: tp.List[float] = [amplitude]
            counter: int = i + 1
            while counter < len(midi_dicts_list) and midi in midi_dicts_list[counter]:
                amplitudes.append(midi_dicts_list[counter][midi])
                midi_dicts_list[counter].pop(midi)
                counter += 1
            if len(amplitudes) == 1:
                continue
            start_index = 0
            previous_amplitude: float = amplitudes[0]
            current_notes: tp.List[Note] = []
            for j in range(1, len(amplitudes)):
                if abs(amplitudes[j] - previous_amplitude) > minimal_difference \
                        and previous_amplitude < amplitudes[j]:
                    duration: float = (j - 1 - start_index)
                    if duration == 0:
                        previous_amplitude = amplitudes[j]
                        start_index = j
                        continue
                    current_notes.append(
                        Note(midi, (i + start_index) * duration_frame, duration * duration_frame,
                             amplitudes[start_index]))
                    start_index = j
                previous_amplitude = amplitudes[j]
            if start_index + 1 < len(amplitudes):
                current_notes.append(
                    Note(midi, (i + start_index) * duration_frame, (len(amplitudes) - 1 - start_index) * duration_frame,
                         amplitudes[start_index]))
            result_notes += current_notes
    return result_notes


def main() -> None:
    duration_frame = 0.25
    midis_and_amplitudes: tp.List[tp.List[tp.Tuple[int, float]]] = get_midis_with_amplitudes(
        "Yiruma - River Flows in You.wav", duration_frame)
    midi_dicts_list: tp.List[tp.Dict[int, float]] = get_dicts_from_lists(midis_and_amplitudes)
    notes: tp.List[Note] = get_notes(midi_dicts_list, duration_frame)


if __name__ == "__main__":
    main()
