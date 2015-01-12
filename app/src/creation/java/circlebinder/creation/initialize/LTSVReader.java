package circlebinder.creation.initialize;

import net.ichigotake.common.util.Optional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import am.ik.ltsv4j.LTSV;

public class LTSVReader<T> {

    private final InputStream inputStream;
    private final LTSVParser<T> parser;
    private final LTSVReadLineListener<T> listener;

    public LTSVReader(InputStream inputStream, LTSVParser<T> parser, LTSVReadLineListener<T> listener) {
        this.inputStream = inputStream;
        this.parser = parser;
        this.listener = listener;
    }

    public void read() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                Optional<T> item = parser.parseLTSV(LTSV.parser().parseLine(line));
                for (T value : item.asSet()) {
                    listener.onLineRead(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(reader);
        }
    }
    
}
