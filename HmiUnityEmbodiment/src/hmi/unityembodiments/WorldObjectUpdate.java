package hmi.unityembodiments;

public class WorldObjectUpdate {
    public String id;
    public float[] data;
    public WorldObjectUpdate(String id, float[] data) {
        this.id = id;
        this.data = data;
    }
}