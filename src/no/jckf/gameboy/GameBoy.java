package no.jckf.gameboy;

public class GameBoy {
	public static void main(String[] args) {
		ROM rom = new ROM("pokemon.gb");
		MMU mmu = new MMU();
		CPU cpu = new CPU();
	}
}
