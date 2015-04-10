package org.toilelibre.libe.soundtransform.ioc;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.ioc.javax.JavaXRootModule;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

enum ImplChooser {

    ANDROID (new String [] { "The Android Project" }, AndroidRootModule.class), JAVA (new String [] { "Oracle Corporation", "Sun Microsystems Inc." }, JavaXRootModule.class);

    public enum ImplChooserErrorCode implements ErrorCode {
        INVALID_RUNTIME ("Invalid Java Runtime : %1s");

        private final String messageFormat;

        ImplChooserErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final Class<? extends ImplAgnosticRootModule> moduleClass;

    private final String []                               acceptValues;

    ImplChooser (final String [] acceptValues1, final Class<? extends ImplAgnosticRootModule> moduleClass1) {
        this.acceptValues = acceptValues1;
        this.moduleClass = moduleClass1;
    }

    @SuppressWarnings ("unchecked")
    static Class<ImplAgnosticRootModule> getCorrectImplModule (final String acceptValue1) {
        for (final ImplChooser runtime : ImplChooser.values ()) {
            if (Arrays.asList (runtime.acceptValues).contains (acceptValue1)) {
                return (Class<ImplAgnosticRootModule>) runtime.moduleClass;
            }
        }
        throw new SoundTransformRuntimeException (ImplChooserErrorCode.INVALID_RUNTIME, new IllegalArgumentException (), acceptValue1);
    }
}
