package wsndes.sim.environment;

import wsndes.sim.network.Packet;

public abstract class NoiseModel {
	public abstract Packet addNoise(Packet p);
}
