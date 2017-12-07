import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import recievedMessages.CheckSelectedSeatsMessage;
import recievedMessages.ReleaseSelectedSeatsMessage;
import recievedMessages.SendBookingInfoMessage;
import recievedMessages.SendDateMessage;
import recievedMessages.SendSelectedMovieMessage;
import sendedMessages.AvailableMovie;
import sendedMessages.AvailableMoviesMessage;
import sendedMessages.BookingInfo;
import sendedMessages.GetSelectedMovieSeatsMessage;
import sendedMessages.RequestStatusMessage;

public class TCPServer {
	static final int BUFSIZE = 1024;
	static final int PORT = 2020;
	private ReadWriteLock lock;
	
	public TCPServer(ReadWriteLock lock) {
		this.lock = lock;
	}
	
	public ReadWriteLock getLock() {
		return this.lock;
	}
	public static void main(String[] args) {
		ReadWriteLock lock = new ReadWriteLock();
		TCPServer server = new TCPServer(lock);
		ServerSocket passiveServer = null;
		Socket clientConnection = null;
		try {
			System.out.println("BBB");
			passiveServer = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while(true) {
				System.out.println("AAA");
				try {
					System.out.println(InetAddress.getByName("0.tcp.ngrok.io").getHostAddress());
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					clientConnection = passiveServer.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}
				new Thread(server.new clientService(clientConnection, server.getLock())).start();
		}

	}
	private class clientService implements Runnable{
		Socket clientConnection;
		BufferedReader in;
		PrintWriter out;
		Gson gson;
		ReadWriteLock lock;
		
		//SAVES THE CLIENT SOCKET TO THE NEW THREAD
		public clientService(Socket clientConnection, ReadWriteLock lock){
			this.clientConnection = clientConnection;
			this.lock = lock;
			this.gson = new Gson();
			try {
				this.in = new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream(),  StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientConnection.getOutputStream(),StandardCharsets.UTF_8)), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(true) {
				String message="";
				//Read the incoming request
				try {
					message = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Message Taken");
				//In this state message should have the json string format of the request.
				JsonObject msgObj = new JsonParser().parse(message).getAsJsonObject();
				String type = msgObj.get("type").getAsString();
				
				if(type.equals("DATE")) {//SendDateMessage --READ
					SendDateMessage m = gson.fromJson(message,SendDateMessage.class);
					lock.lockRead();
					String date = m.getDate();
					File f = new File("./"+date+"/Movies.txt");
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(f));
					} catch (IOException e) {
						e.printStackTrace();
					}
					String movie;
					ArrayList<AvailableMovie> movieArray = new ArrayList<AvailableMovie>();
					try {
						while((movie = reader.readLine()) != null) {
							String[] availableMovieInfo = movie.split("\t");
							AvailableMovie mov = new AvailableMovie(availableMovieInfo[0], availableMovieInfo[1]);
							movieArray.add(mov);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					//SEND MOVIE ARRAY BACK TO THE ANDROID APP
					AvailableMoviesMessage mess = new AvailableMoviesMessage("AVAILABLE_MOVIES", movieArray);
					String movMess = gson.toJson(mess);
					out.println(movMess);
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					lock.unlockRead();
					
				}else if(type.equals("SELECTED_MOVIE")) {//SendSelectedMovieMessage --READ
					SendSelectedMovieMessage m = gson.fromJson(message, SendSelectedMovieMessage.class);
					lock.lockRead();
					AvailableMovie avMov = m.getSelectedMovie();
					String date = m.getDate();
					String movieName = avMov.getName();
					String time = avMov.getTime();
					time = time.replace(':', '.');
					File f = new File("./"+date+"/"+movieName+" "+time+" - Seats.txt");
					//AT THIS MOMENT THE SEATS FILE PATH SHOULD BE THE PATH OF THE FILE F
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(f));
					} catch (IOException e) {
						e.printStackTrace();
					}
					int[][] seatStatus = new int[11][14];
					for(int i=0; i<11; i++) {
						String seatRow = null;
						try {
							seatRow = reader.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						String[] seatStringValues = seatRow.split(" ");
						//ASSIGNING SEAT VALUES TO SEATSTATUS INT ARRAY
						//LENGTH SHOULD ALWAYS BE 14 EVEN FOR THE LAST ROW
						for(int j=0;j<seatStringValues.length; j++) {
							seatStatus[i][j] = Integer.parseInt(seatStringValues[j]);
						}
					}
					//SEND THE SEAT INFORMATION TO THE ANDROID APPLICATION
					GetSelectedMovieSeatsMessage mess = new GetSelectedMovieSeatsMessage("SEATS", seatStatus);
					String seatMess = gson.toJson(mess);
					out.println(seatMess);
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					lock.unlockRead();
					
				}else if(type.equals("SELECTED_SEATS")) {//CheckSelectedSeatsMessage --WRITE
					CheckSelectedSeatsMessage m = gson.fromJson(message, CheckSelectedSeatsMessage.class);
					lock.lockWrite();
					AvailableMovie movie = m.getSelectedMovie();
					String date = m.getDate();
					String movieName = movie.getName();
					String time = movie.getTime();
					time = time.replace(':', '.');
					File f = new File("./"+date+"/"+movieName+" "+time+" - Seats.txt");
					//AT THIS MOMENT THE SEATS FILE PATH SHOULD BE THE PATH OF THE FILE F
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(f));
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
					ArrayList<int[]> selectedSeats = m.getSelectedSeats();
					int[][] seatsValues = new int[11][14];
					for(int i=0; i<11; i++) {
						String seatRow = null;
						try {
							seatRow = reader.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						String[] seatStringValues = seatRow.split(" ");
						//ASSIGNING SEAT VALUES TO SEATSTATUS INT ARRAY
						//LENGTH SHOULD ALWAYS BE 14 EVEN FOR THE LAST ROW
						for(int j=0;j<seatStringValues.length; j++) {
							seatsValues[i][j] = Integer.parseInt(seatStringValues[j]);
						}
					}
					//CLOSE THE READER
					try {
						reader.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					boolean areWantedSeatsEmpty = true;
					//CHECK IF ANY OF THE WANTED SEATS ARE ALREADY TAKEN
					for(int[] seat: selectedSeats) {
						if(seatsValues[seat[0]][seat[1]]==1) {
							areWantedSeatsEmpty = false;
							break;
						}
					}
					String status;
					if(areWantedSeatsEmpty) { //IF NONE TAKEN
						//SET SELECTED SEATS VALUE TO 1
						for(int[] seat: selectedSeats) {
							seatsValues[seat[0]][seat[1]] = 1;
						}
						//FILE WRITER
						BufferedWriter writer = null;
						try {
							writer = new BufferedWriter(new OutputStreamWriter(
								    new FileOutputStream(f), "UTF-8"));
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						//WRITE NEW STATUS OF THE SEATS TO THE FILE
						for(int i=0; i<11; i++) {
							String line = "" + seatsValues[i][0];
							for(int j=1; j<14; j++) {
								line = line + " " + seatsValues[i][j];
							}
							line = line + "\r\n";
							try {
								writer.write(line);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						//CLOSE THE WRITER
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						status = "OK";
					}else { // IF SOME TAKEN
						status = "ERROR";
					}
					RequestStatusMessage mess = new RequestStatusMessage("STATUS", status);
					String statMess = gson.toJson(mess);
					out.println(statMess);
					lock.unlockWrite();
					
				}else if(type.equals("BOOKING_INFOS")) {//SendBookingInfoMessage --WRITE
					SendBookingInfoMessage m = gson.fromJson(message, SendBookingInfoMessage.class);
					lock.lockWrite();
					ArrayList<BookingInfo> infos = m.getBookingInfos();
					AvailableMovie movie = m.getSelectedMovie();
					String date = m.getDate();
					String movieName = movie.getName();
					String time = movie.getTime();
					time = time.replace(':', '.');
					File f = new File("./"+date+"/"+movieName+" "+time+".txt");
					//FILE WRITER
					BufferedWriter writer = null;
					try {
						writer = new BufferedWriter(new OutputStreamWriter(
							    new FileOutputStream(f,true), "UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					for(BookingInfo info : infos) {
						String bookingInfo = info.getSeatID() + " - "+ info.getName() + " " + info.getSurname() + "\r\n";
						try {
							writer.write(bookingInfo);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//CLOSE WRITER
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					RequestStatusMessage mess = new RequestStatusMessage("STATUS", "OK");
					String statMess = gson.toJson(mess);
					out.println(statMess);
					lock.unlockWrite();
					
				}else if(type.equals("RELEASE_SEATS")) {//ReleaseSelectedSeatsMessage --WRITE
					ReleaseSelectedSeatsMessage m = gson.fromJson(message, ReleaseSelectedSeatsMessage.class);
					lock.lockWrite();
					AvailableMovie movie = m.getSelectedMovie();
					String date = m.getDate();
					String movieName = movie.getName();
					String time = movie.getTime();
					time = time.replace(':', '.');
					File f = new File("./"+date+"/"+movieName+" "+time+" - Seats.txt");
					//AT THIS MOMENT THE SEATS FILE PATH SHOULD BE THE PATH OF THE FILE F
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(f));
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
					ArrayList<int[]> selectedSeats = m.getSelectedSeats();
					int[][] seatsValues = new int[11][14];
					for(int i=0; i<11; i++) {
						String seatRow = null;
						try {
							seatRow = reader.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						String[] seatStringValues = seatRow.split(" ");
						//ASSIGNING SEAT VALUES TO SEATSTATUS INT ARRAY
						//LENGTH SHOULD ALWAYS BE 14 EVEN FOR THE LAST ROW
						for(int j=0;j<seatStringValues.length; j++) {
							seatsValues[i][j] = Integer.parseInt(seatStringValues[j]);
						}
					}
					//CLOSE THE READER
					try {
						reader.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					
					//CHANGE SEAT VALUES TO 0
					for(int[] seat : selectedSeats) {
						seatsValues[seat[0]][seat[1]] = 0;
					}
					
					//WRITE NEW SEAT VALUES
					//FILE WRITER
					BufferedWriter writer = null;
					try {
						writer = new BufferedWriter(new OutputStreamWriter(
							    new FileOutputStream(f), "UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					//WRITE NEW STATUS OF THE SEATS TO THE FILE
					for(int i=0; i<11; i++) {
						String line = "" + seatsValues[i][0];
						for(int j=1; j<14; j++) {
							line = line + " " + seatsValues[i][j];
						}
						line = line + "\r\n";
						try {
							writer.write(line);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//CLOSE THE WRITER
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				
					//SEND OK TO APPLICATION
					RequestStatusMessage mess = new RequestStatusMessage("STATUS", "OK");
					String statMess = gson.toJson(mess);
					out.println(statMess);
					lock.unlockWrite();
				}
			}
		}
	}
}

