package com.github.atasro2.clebybot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class MessageListener extends ListenerAdapter {

	String[] sanics = {
			"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSt1Uo0pp4k7lC3G8P2hG3rPS0xGH8VmaG2d9JQBSp63gB6-vXv",
			"http://tse3.mm.bing.net/th?id=OIP.sQ9TYZFM2LzvUEIZdl91AwHaGt",
			"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRipckbiggPI3MCPHreu2Tao-QNN9MBPrM_hr_aYZarxWl6qzne",
			"https://pm1.narvii.com/6715/b9dc06d493c1daf6101fe8818c404647972aeba6_hq.jpg",
			"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSt1Uo0pp4k7lC3G8P2hG3rPS0xGH8VmaG2d9JQBSp63gB6-vXv",
			"https://pm1.narvii.com/6496/326fad2731e4aa21b6b3371d8e595fe6a407e4ce_hq.jpg",
			"http://i0.kym-cdn.com/photos/images/newsfeed/001/125/930/08c.jpg" };
	Emote emote;
	String Prefix = "!";
	private static AudioPlayerManager playerManager;
	private static Map<Long, GuildMusicManager> musicManagers;
	private static SearchResult singleVideo;
	private static ResourceId rId;

	public static void main(String[] args) throws LoginException, RateLimitedException {
		System.setProperty("http.agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
		JDA jda = new JDABuilder(AccountType.BOT).setToken("").buildAsync();
		jda.getPresence().setGame(Game.of(GameType.STREAMING, "Im ScykohBots wife"));
		musicManagers = new HashMap<>();

		playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
		jda.addEventListener(new MessageListener());
		
	}

	private boolean isMaintenance;

	private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		long guildId = Long.parseLong(guild.getId());
		GuildMusicManager musicManager = musicManagers.get(guildId);

		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager);
			musicManagers.put(guildId, musicManager);
		}

		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

		return musicManager;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (isMaintenance == false) {
			Message message = event.getMessage();
			String[] content = message.getContentRaw().split(" ");
			Guild guild = event.getGuild();
			if (content[0].equals(this.Prefix + "user")) {

				Member memb;

				memb = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));

				System.out.println(memb);

				String NAME = memb.getEffectiveName();
				String TAG = memb.getUser().getName() + "#" + memb.getUser().getDiscriminator();
				String GUILD_JOIN_DATE = memb.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME);
				String DISCORD_JOINED_DATE = memb.getUser().getCreationTime()
						.format(DateTimeFormatter.RFC_1123_DATE_TIME);
				String ID = memb.getUser().getId();
				String STATUS = memb.getOnlineStatus().getKey();
				String ROLES = "";
				String GAME;
				String AVATAR = memb.getUser().getAvatarUrl();

				try {
					GAME = memb.getGame().getName();
				} catch (Exception e) {
					GAME = "-/-";
				}

				for (Role r : memb.getRoles()) {
					ROLES += r.getName() + ", ";
				}
				if (ROLES.length() > 0)
					ROLES = ROLES.substring(0, ROLES.length() - 2);
				else
					ROLES = "No roles on this server.";

				if (AVATAR == null) {
					AVATAR = "No Avatar";
				}

				EmbedBuilder em = new EmbedBuilder().setColor(Color.green.brighter());
				em.setDescription(":spy:   **User information for " + memb.getUser().getName() + ":**")
						.addField("Name / Nickname", NAME, false).addField("User Tag", TAG, false)
						.addField("ID", ID, false).addField("Current Status", STATUS, false)
						.addField("Current Game", GAME, false).addField("Roles", ROLES, false)
						.addField("Guild Joined", GUILD_JOIN_DATE, false)
						.addField("Discord Joined", DISCORD_JOINED_DATE, false).addField("Avatar-URL", AVATAR, false);

				if (AVATAR != "No Avatar") {
					em.setThumbnail(AVATAR);
				}

				event.getTextChannel().sendMessage(em.build()).complete();
			}

			if (event.isFromType(ChannelType.TEXT)) {
				System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(), event.getChannel().getName(),
						event.getAuthor(), event.getMessage().getContentDisplay());
			} else {
				System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
			}
			if (event.getAuthor().isBot())
				return;
			if (content[0].equals(this.Prefix + "ping")) {
				MessageChannel channel = event.getChannel();
				channel.sendMessage("Pong!").queue(); // Important to call
														// .queue()
														// on the RestAction
														// returned by
														// sendMessage(...)
			}
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			if (content[0].equals(this.Prefix + "time")) {
				MessageChannel channel = event.getChannel();
				channel.sendMessage(sdf.format(cal.getTime())).queue(); // Important
																		// to
																		// call
																		// .queue()
																		// on
																		// the
																		// RestAction
																		// returned
																		// by
																		// sendMessage(...)
				channel.addReactionById(content.toString(), event.getGuild().getEmoteById("436138591056429066"));
			}
			if (content[0].equals(this.Prefix + "sanic")) {
				MessageChannel channel = event.getChannel();
				channel.sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7))
						.setDescription("RUNNING AT THE SPEED OF SOUND!").setImage(getWord(sanics)).build()).queue(); // Important
				// to
				// sendMessage(...)
				message.addReaction(this.emote.getGuild().getEmoteById(448830753959313409L));
			}
			if (content[0].equals("hi")) {
				message.addReaction(message.getGuild().getEmoteById("436138448307486722")).queue();
			}
			if (content[0].equals(this.Prefix + "catfact")) {
				event.getTextChannel()
						.sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7)).setDescription(get()).build())
						.queue();
			}
			if (content[0].equals(this.Prefix + "dogfact")) {
				event.getTextChannel()
						.sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7)).setDescription(getdog()).build())
						.queue();
			}
			if (content[0].equals(this.Prefix + "strawpoll")) {
				MessageChannel channel = event.getChannel();
				StrawPoll strawPoll = new StrawPoll(String.valueOf(content[1]).replace("_", " "),
						String.valueOf(content[2]), String.valueOf(content[3]));
				strawPoll.create(); // Creates the StrawPoll, by sending a
									// request
									// to the API
				channel.sendMessage(strawPoll.getPollURL()).queue();
			}
			if (content[0].equals(this.Prefix + "floof")) {
				event.getTextChannel()
						.sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7)).setDescription("So kawaiiii")
								.setImage(getstr("https://randomfox.ca/floof", "image").replace("\\", "")).build())
						.queue();
			}
			if (content[0].equals(this.Prefix + "meow")) {
				event.getTextChannel()
						.sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7))
								.setDescription("Oh hey narus relatives")
								.setImage(getstr("https://aws.random.cat/meow", "file").replace("\\", "")).build())
						.queue();
			}

			if (content[0].equals(this.Prefix + "dadjoke")) {
				event.getTextChannel().sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7))
						.setDescription(getstr("http://icanhazdadjoke.com/", "joke")).build()).queue();
			}

			if (guild != null) {
				if (content[0].equals("!" + "play") && content.length == 2) {
					loadAndPlay(event.getTextChannel(), content[1]);
				} else if (content[0].equals(this.Prefix + "skip")) {
					skipTrack(event.getTextChannel());
				} else if (content[0].equals(this.Prefix + "volume")) {
					volume(event.getTextChannel(), Integer.valueOf(content[1]));
				}
			}

			if (content[0].equals(this.Prefix + "maintenance")) {
				Member memb;

				memb = event.getMember();
				if (memb.getUser().getIdLong() == 332181179627339777L
						|| memb.getUser().getIdLong() == 324661689972686849L) {
					event.getTextChannel().sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7))
							.setDescription("The bot is now in maintenance !").build()).queue();
					isMaintenance = true;
				} else {
					event.getTextChannel().sendMessage("Sorry but you are not MCboy or coltonrawr!").queue();
				}
			}
		}

		else {
			Message message = event.getMessage();
			String[] content = message.getContentRaw().split(" ");
			if (content[0].equals(this.Prefix + "help")) {
				event.getTextChannel()
						.sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7))
								.setDescription("the bot is in maintenance at the moment please try later").build())
						.queue();
			}
			if (content[0].equals(this.Prefix + "maintenance")) {
				Member memb;

				memb = event.getMember();
				System.out.println(memb.getUser().getIdLong());
				if (memb.getUser().getIdLong() == 332181179627339777L
						|| memb.getUser().getIdLong() == 324661689972686849L) {
					event.getTextChannel().sendMessage(new EmbedBuilder().setColor(new Color(0xFF00C7))
							.setDescription("The bot is now out of maintenance !").build()).queue();
					isMaintenance = false;
				} else {
					event.getTextChannel().sendMessage("Sorry but you are not MCboy or coltonrawr!").queue();
				}
			}
		}
	}

	private void loadAndPlay(final TextChannel channel, final String urlOrYtName) {
		final String[] trackUrl = urlOrYtName.split(":");
		String url = urlOrYtName.toString();
		System.out.println(url);
		if (trackUrl[0].equalsIgnoreCase("https") || trackUrl[0].equalsIgnoreCase("http")) {
			final GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
			if (musicManager.player.getVolume() != 25)
			{
				musicManager.player.setVolume(25);
			}
			playerManager.loadItemOrdered(musicManager, urlOrYtName, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					long dur = track.getInfo().length;
					channel.sendMessage(new EmbedBuilder().setDescription("Added to queue " + track.getInfo().title).addField("Duration", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(dur)/60) + " Minutes", false).setColor(new Color(0xFF00C7)).build()).queue();

					play(channel.getGuild(), musicManager, track);
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					AudioTrack firstTrack = playlist.getSelectedTrack();

					if (firstTrack == null) {
						firstTrack = playlist.getTracks().get(0);
					}

					channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist "
							+ playlist.getName() + ")").queue();

					play(channel.getGuild(), musicManager, firstTrack);
				}

				@Override
				public void noMatches() {
					channel.sendMessage("Nothing found by " + trackUrl).queue();
				}

				@Override
				public void loadFailed(FriendlyException exception) {
					exception.printStackTrace();
					channel.sendMessage("Could not play: " + exception.getMessage()).queue();
				}
			});
		}
		else
		{
			YouTubeSearch.getVideo(url);
			String ytUrl = "https://www.youtube.com/watch?v="+rId.getVideoId();
			String ytThumbnail = "http://img.youtube.com/vi/" + rId.getVideoId() +"/0.jpg";
			final GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

			playerManager.loadItemOrdered(musicManager, ytUrl, new AudioLoadResultHandler() {

				public void trackLoaded(AudioTrack track) {
					long dur = track.getInfo().length;
					channel.sendMessage(new EmbedBuilder().setDescription("Added to queue " + track.getInfo().title).addField("Duration", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(dur)/60) + " Minutes", false).setThumbnail(ytThumbnail).setColor(new Color(0xFF00C7)).build()).queue();

					play(channel.getGuild(), musicManager, track);
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					AudioTrack firstTrack = playlist.getSelectedTrack();

					if (firstTrack == null) {
						firstTrack = playlist.getTracks().get(0);
					}

					channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist "
							+ playlist.getName() + ")").queue();

					play(channel.getGuild(), musicManager, firstTrack);
				}

				@Override
				public void noMatches() {
					channel.sendMessage("Nothing found by " + trackUrl).queue();
				}

				@Override
				public void loadFailed(FriendlyException exception) {
					exception.printStackTrace();
					channel.sendMessage("Could not play: " + exception.getMessage()).queue();
				}

			});
		}
	} 


	private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
		connectToFirstVoiceChannel(guild.getAudioManager());

		musicManager.scheduler.queue(track);
	}

	private void volume(TextChannel channel, int volume) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.player.setVolume(volume);

		channel.sendMessage("Set Volume to " + musicManager.player.getVolume()).queue();
	}

	private void skipTrack(TextChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.scheduler.nextTrack();

		channel.sendMessage("Skipped to next track.").queue();
	}

	private static void connectToFirstVoiceChannel(AudioManager audioManager) {
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
				audioManager.openAudioConnection(voiceChannel);
				break;
			}
		}
	}

	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = rd.readLine();
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private String get() {

		String out = "";

		try {
			out = readJsonFromUrl("http://fact.birb.pw/api/v1/cat").getString("string");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return out;
	}

	private String getdog() {

		String out = "";

		try {
			out = readJsonFromUrl("http://fact.birb.pw/api/v1/dog").getString("string");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return out;
	}

	private String getstr(String url, String str) {

		String out = "";

		try {
			out = readJsonFromUrl(url).getString(str);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return out;
	}

	public static String getWord(String[] words) {
		Random r = new Random();
		String word = words[r.nextInt(words.length)];
		return word;
	}
	public static void setVar(Iterator<SearchResult> iteratorSearchResults, String query) {
        singleVideo = iteratorSearchResults.next();
        rId = singleVideo.getId();	
	}

}
