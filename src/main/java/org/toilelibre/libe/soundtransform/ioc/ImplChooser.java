package org.toilelibre.libe.soundtransform.ioc;

import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.ioc.javax.JavaXRootModule;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

enum ImplChooser {

    ANDROID ("The Android Project", AndroidRootModule.class),
    JAVA    ("Oracle Corporation", JavaXRootModule.class);
    
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
    private final String acceptValue;

    ImplChooser (String acceptValue1, Class<? extends ImplAgnosticRootModule> moduleClass1) {
        this.acceptValue = acceptValue1;
        this.moduleClass = moduleClass1;
    }
    
    @SuppressWarnings("unchecked")
    static Class<ImplAgnosticRootModule> getCorrectImplModule (String acceptValue1){
        for (final ImplChooser runtime : ImplChooser.values ()){ 
            if (runtime.acceptValue.equals (acceptValue1)){
                return (Class<ImplAgnosticRootModule>) runtime.moduleClass;
            }
        }
        throw new SoundTransformRuntimeException (ImplChooserErrorCode.INVALID_RUNTIME, new IllegalArgumentException (),
                acceptValue1);
    }
}
