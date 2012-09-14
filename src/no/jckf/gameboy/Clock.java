package no.jckf.gameboy;

public class Clock {
	private int target = 4194304 / 1000;
	private int current = 0;
	private long timestamp = 0;
	private int retardation = 0;

	public void tick() {
		if (retardation > 0) {
			int r = retardation;
			while (r-- > 0) Thread.currentThread().checkAccess();
		}

		current++;

		long time = System.currentTimeMillis();
		if (time / 1000 > timestamp / 1000) {
			System.out.println((current / 1000D) + " MHz @ " + retardation);
		}
		if (time > timestamp) {
			if (current > target) {
				retardation++;
			} else if (current < target && retardation > 0) {
				retardation--;
			}

			current = 0;
			timestamp = time;
		}
	}
}
