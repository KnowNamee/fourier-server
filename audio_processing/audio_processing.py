import argparse
import subprocess
from pathlib import Path

import mido
from pydub import AudioSegment


def process_audio(infile: str, outfile: str) -> None:
    if Path(infile).suffix == ".mp3":
        sound = AudioSegment.from_mp3(str(Path(
            __file__).parent.absolute()) + "/" + infile)
        sound.export(str(Path(
            __file__).parent.absolute()) + "/" + infile[:-4] + ".wav", format="wav")
        infile = infile[:-4] + ".wav"
    switch_to_directory: str = "cd " + "'" + str(Path(
        __file__).parent.absolute()) + "'" + "\n"
    subprocess.run([switch_to_directory + "audio-to-midi '" + infile + "' -b 120 -t 250 -T -12 -s -n"], shell=True)
    mid_file = str(Path(__file__).parent.absolute()) + "/" + str(Path(infile).name) + ".mid"
    initial = mido.MidiFile(mid_file, clip=True)
    result_midi = mido.MidiFile()
    result_midi.add_track('')
    result_midi.clip = True
    result_midi.ticks_per_beat = initial.ticks_per_beat
    for note in initial.tracks[0]:
        if note.type != "end_of_track":
            velocity = note.velocity
            velocity = 60 if velocity else 0
            if note.note < 70 and velocity:
                velocity = 40
            note = mido.Message.from_dict(
                {"type": note.type, "channel": note.channel, "note": note.note, "velocity": velocity,
                 "time": note.time})
            result_midi.tracks[0].append(note)
        else:
            result_midi.tracks[0].append(note)
    output_midi = str(Path(__file__).parent.absolute()) + "/" + "output.midi"
    result_midi.save(output_midi)
    subprocess.run([switch_to_directory + "musescore -o '" + outfile + "' " + output_midi], shell=True)
    subprocess.run([switch_to_directory + "rm " + output_midi], shell=True)
    subprocess.run([switch_to_directory + "rm " + mid_file], shell=True)
    subprocess.run([switch_to_directory + "rm " + infile], shell=True)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("infile", help="Path to input file")
    parser.add_argument("outfile", help="Path to output file")
    args = parser.parse_args()
    process_audio(args.infile, args.outfile)
