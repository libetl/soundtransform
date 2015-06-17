package org.toilelibre.libe.soundtransform.idetests;

import org.apache.commons.math3.complex.Complex;
import org.junit.Ignore;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.QuickChart;
import com.xeiam.xchart.SwingWrapper;

/**
 * To be used only in a IDE environment
 *
 */
@SuppressWarnings ("unchecked")
@Ignore //to run the tests, comment this line, and keep the tests debugging after being run
public class ViewCepstrumIDETest {

    @Test
    public void viewCepstrumOfPianoNotes () throws SoundTransformException {
        Chart chart = this.getChart (new String [] { "C4", "D4", "E4", "F4", "G4", "A5", "B5", "C5" }, $.select (SpectrumToCepstrumHelper.class)
                .spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]),
                $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]),
                $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano3e.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]),
                $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano4f.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]),
                $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano5g.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]),
                $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano6a.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]),
                $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano7b.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]),
                $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (FluentClient.start ().withClasspathResource ("piano8c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]));
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoC4 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "C4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoD4 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "D4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoE4 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano3e.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "E4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoF4 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano4f.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "F4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoG4 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano5g.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "G4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoA5 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano6a.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "A5" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoB5 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano7b.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "B5" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoC5 () throws SoundTransformException {
        Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano8c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "C5" }, cepstrum)).displayChart ();
    }

    private Chart getChart (String [] notes, Spectrum<Complex []>... fs) {
        double [] xData = new double [fs [0].getState ().length];
        double [][] yData = new double [fs.length] [fs [0].getState ().length];
        final float timelapseInTheCepstrum = fs [0].getState ().length * 1.0f / fs [0].getSampleRate ();
        for (int i = 10 ; i < 1000 ; i++) {
            xData [i] = 1.0 / (i * 1.0 / fs [0].getState ().length * timelapseInTheCepstrum);
        }
        for (int j = 0 ; j < fs.length ; j++) {
            for (int i = 10 ; i < 1000 ; i++) {
                yData  [j] [i] = (i > 10 && i < fs [j].getState ().length - 10 ? fs [j].getState () [i].abs () : 0);
            }
        }
        return QuickChart.getChart ("Cepstrums", "hz", "ampl", notes, xData, yData);

    }
}
