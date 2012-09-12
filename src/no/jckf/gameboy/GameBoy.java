package no.jckf.gameboy;

public class GameBoy {
	public static void main(String[] args) {
		ROM rom = new ROM("bgb/pokemon.gb");
		MMU mmu = new MMU();
		CPU cpu = new CPU();

		// Hi-tech clock speed control...
		while (cpu.cycle());
	}
}
