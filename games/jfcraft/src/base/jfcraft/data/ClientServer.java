package jfcraft.data;

/** Caches common client/server data.
 *
 * @author pquiring
 */

public class ClientServer {
  public boolean isClient, isServer;
  public World world;

  public ClientServer(boolean isClient) {
    if (isClient) {
      this.isClient = true;
      this.isServer = false;
      world = Static.client.world;
    } else {
      this.isClient = false;
      this.isServer = true;
      world = Static.server.world;
    }
  }
}
