package de.michaelpohl.loopy;

public interface JNIListener {
    void onAcceptMessage(String string);

    void onAcceptMessageVal(int messVal);
}
