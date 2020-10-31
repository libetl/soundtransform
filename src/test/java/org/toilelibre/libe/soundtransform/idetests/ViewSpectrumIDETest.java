package org.toilelibre.libe.soundtransform.idetests;

import java.awt.Color;

import org.apache.commons.math3.complex.Complex;
import org.junit.Ignore;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.LevelSoundTransform;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NormalizeSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

// to run the tests, comment this line, and keep the tests debugging
// after being run
@Ignore
public class ViewSpectrumIDETest {

    @Test
    public void viewC3 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "C3 (260Hz)" }, FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewD3 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "D3 (293Hz)" }, FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewE3 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "E3 (329Hz)" }, FluentClient.start ().withClasspathResource ("piano3e.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewHA3PlusC3 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "A3 (221Hz) + C3 (260Hz)" }, FluentClient.start ().withClasspathResource ("gpiano3.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewF3 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "F3 (349Hz)" }, FluentClient.start ().withClasspathResource ("piano4f.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewG3 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "G3 (391Hz)" }, FluentClient.start ().withClasspathResource ("piano5g.wav").convertIntoSound ().apply (new LevelSoundTransform (100)).apply (new NormalizeSoundTransform (1)).splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewA4 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "A4 (440Hz)" }, FluentClient.start ().withClasspathResource ("piano6a.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewB4 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "B4 (493Hz)" }, FluentClient.start ().withClasspathResource ("piano7b.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewC4 () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "C4 (523Hz)" }, FluentClient.start ().withClasspathResource ("piano8c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    @Test
    public void viewAll () throws SoundTransformException {
        final XYChart chart = this.getChart (new String [] { "C3", "D3", "E3", "F3", "G3", "A4", "B4", "C4" }, FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0],
                FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0], FluentClient.start ().withClasspathResource ("piano3e.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0],
                FluentClient.start ().withClasspathResource ("piano4f.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0], FluentClient.start ().withClasspathResource ("piano5g.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0],
                FluentClient.start ().withClasspathResource ("piano6a.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0], FluentClient.start ().withClasspathResource ("piano7b.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0],
                FluentClient.start ().withClasspathResource ("piano8c.wav").convertIntoSound ().splitIntoSpectrums ().stopWithSpectrums ().get (0) [0]);
        new SwingWrapper (chart).displayChart ();
    }

    private XYChart getChart (final String [] notes, final Spectrum<?>... spectrums) {
        final double [] xData = new double [ ((Complex []) spectrums [0].getState ()).length];
        final double [][] yData = new double [spectrums.length] [ ((Complex []) spectrums [0].getState ()).length];
        for (int i = 10 ; i < 1000 ; i++) {
            xData [i] = $.select (SpectrumHelper.class).freqFromSampleRate (i, ((Complex []) spectrums [0].getState ()).length * 2, spectrums [0].getSampleRate ());
        }
        for (int j = 0 ; j < spectrums.length ; j++) {
            for (int i = 10 ; i < 1000 ; i++) {
                yData [j] [i] = i > 10 && i < ((Complex []) spectrums [j].getState ()).length - 10 ? ((Complex []) spectrums [j].getState ()) [i].abs () : 0;
            }
        }
        final XYChart chart = QuickChart.getChart ("Spectrum", "f (Hz)", "ampl", notes, xData, yData);

        chart.getStyler ().setChartBackgroundColor (Color.YELLOW);
        chart.getStyler ().setPlotBackgroundColor (Color.YELLOW);
        chart.getStyler ().setLegendBackgroundColor (Color.YELLOW);
        chart.getStyler ().setChartFontColor (Color.YELLOW);
        chart.getStyler ().setPlotGridLinesColor (Color.LIGHT_GRAY);
        chart.getStyler ().setChartFontColor (Color.BLACK);
        chart.getStyler ().setAxisTickLabelsColor (Color.BLACK);
        return chart;

    }
}
