package br.usp.icmc.ssc01032015;
import br.usp.icmc.ssc01032015.players.*;
import java.io.*;
import java.util.*;

public class Simulation {	
    private int round;
    private int typeCounter;
    private State simState;
    private final PrintWriter logFile;
    private HashMap<Competitor, PlayerInfo> players;    
    
    public enum State { NOT_STARTED, RUNNING, FINISHED, ERROR };

    /**
     * Classe usada para guardar informações extras sobre cada jogador durante
     * a simulação.
     */
    private class PlayerInfo {
        public int Id;
        public int Type;

        public PlayerInfo() {
            this.Id = -1;
            this.Type = -1;
        }

        public PlayerInfo(int type, int id) {
            this.Id = id;
            this.Type = type;
        }
    }

    private class PlayerComparator implements Comparator<Competitor> {
        public int compare(Competitor l, Competitor r) {
            return -Double.compare(l.getTotalCash(), r.getTotalCash());
        }
    }

    public Simulation() {
    	this.logFile = null;
    	this.round = 0;
    	this.typeCounter = 0;
    	this.simState = State.NOT_STARTED;
    	this.players = new HashMap<Competitor, PlayerInfo>();
    }
    
    public Simulation(File logFile) throws FileNotFoundException {
    	if(logFile == null) {
    		throw new FileNotFoundException("Null file!");
    	} else {
    		this.logFile = new PrintWriter(logFile);
    		this.logFile.println("SIMULATION CREATED");
    	}
    	
    	this.round = 0;
    	this.typeCounter = 0;
    	this.simState = State.NOT_STARTED;
    	this.players = new HashMap<Competitor, PlayerInfo>();
	}
    
    /**
     * Imprime o placar atual do jogo.
     */
    public void printScore(PrintWriter out) {
        out.format("SCORE OF %d PLAYERS%n", players.size());
        out.println("ID\tCASH\tTYPE");
    	
        // Cria uma lista com os jogadores
    	List<Competitor> score = new ArrayList<Competitor>(players.keySet());

    	// Ordena os jogadores com base no total de cash
    	Collections.sort(score, new PlayerComparator());

    	// Imprime os resultados
    	for(Competitor c : score) {
    		out.format("%d.%d\t%.2f\t(%s)%n", players.get(c).Type,
                    players.get(c).Id, c.getTotalCash(),
                    c.getClass().getSimpleName());
    	}
        out.flush();
    }
    
    /**
     * Termina a smulação, fechando os arquivos de log e imprimindo o placar.
     */
    public void finish() {
    	if(logFile != null) {
    		logFile.println("SIMULATION FINISHED\n");
    		printScore(logFile);
    		logFile.close();
    	}
    	
    	simState = State.FINISHED;
    	printScore(new PrintWriter(System.out));
	}
    
    /**
     * Adiciona jogadores na simulação.
     *
     * Deve ser chamada apenas antes de ser dado inicio a simulação.
     *
     * @param c Classe do competidor a ser adicionado.
     * @param n Quantas instâncias devem ser adicionadas.
     */
    public void addPlayers(Class<? extends Competitor> c, int n) {
    	if(simState != State.NOT_STARTED) {
    		System.err.println("ERROR: Cannot add player after " +
                               "simulation started!");
    		return;
    	}
    	
        for(int i = 0; i < n; ++i) {
            try {
                players.put(c.newInstance(), new PlayerInfo(typeCounter, i));
                if(logFile != null) {
                    logFile.format("ADD PLAYER %d.%d %n", typeCounter, i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        typeCounter++;
    }
    
    /**
     * Inicia a simulação, permitindo chamadas subsequentes da função step(n)
     */
    public void begin() {
		if(simState != State.NOT_STARTED) {
			System.err.println("ERROR: Simulation already started!");
    		return;
		}
		
		assert(round == 0);
        round = 1;
		
		if(players.size() < 2) {
			System.err.println("WARNING: Begining a simulation " +
                               "with less than 2 players!");
		}
		
		if(logFile != null) {
			logFile.println("SIMULATION STARTED");
		}
		
		simState = State.RUNNING;
	}
    
    /**
     * Executa a quantidade dada de rodadas.
     *
     * @param nSteps Numero de rodadas a serem simuladas
     */
    public void step(int nSteps) {
    	if(simState != State.RUNNING) {
    		System.err.println("ERROR: Can't step a simulation that is not" +
                               " runnig!");
    		return;
    	}
    	
    	for(int i = 0; i < nSteps; ++i) step();
    }
    
    /**
     * Executa uma rodada da simulação.
     */
    private void step() {
    	ArrayList<Competitor> toInteract = 
            new ArrayList<Competitor>(players.keySet()); 
    	
    	if(logFile != null) {
    		logFile.println("\nROUND " + round);
    	}
    	
    	for(Competitor a : players.keySet()) {
    		toInteract.remove(a); // Impede doações para si mesmo
    		
    		for(Competitor b : toInteract) {
    			double atob = a.declareDonationTo(b);
    			double btoa = b.declareDonationTo(a);

                // Limitando os valores de doação [0, 10]
                atob = atob > 10 ? 10 : atob < 0 ? 0 : atob;
                btoa = btoa > 10 ? 10 : btoa < 0 ? 0 : btoa;
    			
                //TODO: Acompanhar o 'total cash' de cada jogador

    			a.informDonationFrom(b, btoa);
    			b.informDonationFrom(a, atob);
    			
    			a.addCash((10 - atob) + btoa * 2);
    			b.addCash((10 - btoa) + atob * 2);
    			
    			if(logFile != null) {
    				logFile.format("DONATION\t%d.%d\t%d.%d\t%f%n",
    						players.get(a).Type, players.get(a).Id,
    						players.get(b).Type, players.get(b).Id, atob);
    				
    				logFile.format("DONATION\t%d.%d\t%d.%d\t%f%n",
    						players.get(b).Type, players.get(b).Id,
    						players.get(a).Type, players.get(a).Id, btoa);
    			}
    		}
    	}
    	
    	++round;
    }
    
    public static void main(String args[]) {
        try {
            Simulation s;

            if(args.length < 6 || args.length > 7) {
                System.err.println("Argumentos inválidos!");
                System.err.println("Argumentos esperados: <rodadas> <Dummy>" +
                        " <CopyCat> <TitTat> <MeanTitTat> <Crazy> [logFile]");
                return;
            } else if(args.length == 7) {
                s = new Simulation(new File(args[6]));
            } else {
                s = new Simulation();
            }

            s.addPlayers(Dummy.class, Integer.decode(args[1]));
            s.addPlayers(CopyCat.class, Integer.decode(args[2]));
            s.addPlayers(TitTat.class, Integer.decode(args[3]));
            s.addPlayers(MeanTitTat.class, Integer.decode(args[4]));
            s.addPlayers(Crazy.class, Integer.decode(args[5]));

            s.begin();
            s.step(Integer.decode(args[0]));
            s.finish();
        } catch(NumberFormatException e) {
            System.err.println("Argumentos inválidos! Números malformados!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
