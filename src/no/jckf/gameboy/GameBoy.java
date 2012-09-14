package no.jckf.gameboy;

public class GameBoy {
	private boolean power = true;

	public static void main(String[] args) {
		new GameBoy();
	}

	public GameBoy() {
		ROM rom = new ROM("bgb/pokemon.gb");
		MMU mmu = new MMU();
		CPU cpu = new CPU();
		Clock clock = new Clock();

		while (power) {
			clock.tick();
			cpu.cycle();
		}
	}
}
