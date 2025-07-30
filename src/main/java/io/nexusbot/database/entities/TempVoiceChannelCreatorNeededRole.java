// package io.nexusbot.database.entities;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.hibernate.annotations.OnDelete;
// import org.hibernate.annotations.OnDeleteAction;
//
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
//
// @Entity
// @Table
// public class TempVoiceChannelCreatorNeededRole {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private int id;
//
//     @OneToOne
//     @JoinColumn(name = "id")
//     private long tempVoiceChannelCreatorId;
//
//     @OneToOne
//     @JoinColumn(name = "guild_id")
//     @OnDelete(action = OnDeleteAction.CASCADE)
//     private GuildInfo guild;
//
//     @ManyToOne
//     @JoinColumn(name = "id")
//     private List<GuildRole> neededRoles = new ArrayList<>();
// }
