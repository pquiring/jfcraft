package jfcraft.server;

public interface VoIPEventHandler {
  public static final int DIGIT = 1;  //a DTMF digit receieved
  public static final int SOUND = 2;  //sound playback complete
  public void event(VoIPCallDetails cd, int type, char digit, boolean interrupted);
  public void samples(VoIPCallDetails cd, short sam[]);
}
