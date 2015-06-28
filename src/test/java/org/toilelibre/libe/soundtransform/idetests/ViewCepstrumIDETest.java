package org.toilelibre.libe.soundtransform.idetests;

import java.awt.Color;

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
@Ignore //to run the tests, comment this line, and keep the tests debugging
// after being run
public class ViewCepstrumIDETest {

    @Test
    public void viewCepstrumOfPianoNotes () throws SoundTransformException {
        final Chart chart = this.getChart (new String [] { "C3", "D3", "E3", "F3", "G3", "A4", "B4", "C4" }, $.select (SpectrumToCepstrumHelper.class)
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
    public void viewCepstrumOfPianoC3 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "C3" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoD3 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "D3" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoE3 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano3e.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "E3" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoF3 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano3f.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "F3" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoG3 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano4g.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "G3" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoA4 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano6a.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "A4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoB4 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano7b.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "B4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfPianoC4 () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("piano8c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "C4" }, cepstrum)).displayChart ();
    }

    @Test
    public void viewCepstrumOfA3PlusC3Combo () throws SoundTransformException {
        final Spectrum<?> spectrum = FluentClient.start ().withClasspathResource ("gpiano3.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0];
        final Spectrum<Complex []> cepstrum = $.select (SpectrumToCepstrumHelper.class).spectrumToCepstrum (spectrum);
        new SwingWrapper (this.getChart (new String [] { "A3 (221Hz) + C3 (260Hz)" }, cepstrum)).displayChart ();
    }
    
    private Chart getChart (final String [] notes, final Spectrum<Complex []>... fs) {
        final double [] xData = new double [fs [0].getState ().length];
        final double [][] yData = new double [fs.length] [fs [0].getState ().length];
        final float timelapseInTheCepstrum = fs [0].getState ().length * 1.0f / fs [0].getSampleRate ();
        for (int i = 10 ; i < 1000 ; i++) {
            xData [i] = 1.0 / (i * 1.0 / fs [0].getState ().length * timelapseInTheCepstrum);
        }
        for (int j = 0 ; j < fs.length ; j++) {
            for (int i = 10 ; i < 1000 ; i++) {
                yData [j] [i] = i > 10 && i < fs [j].getState ().length - 10 ? fs [j].getState () [i].abs () : 0;
            }
        }
        final Chart chart = QuickChart.getChart ("Cepstrums", "f (Hz)", "ampl", notes, xData, yData);
        chart.setBackgroundColor (Color.BLACK);
        chart.setLegendBackgroundColor (Color.BLACK);
        chart.setForegroundColor (Color.BLACK);
        chart.setGridLinesColor (Color.LIGHT_GRAY);
        chart.setFontColor (Color.WHITE);
        return chart;

    }
}
