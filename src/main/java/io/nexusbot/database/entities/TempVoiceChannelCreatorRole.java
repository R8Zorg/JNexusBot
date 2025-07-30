// package io.nexusbot.database.entities;
//
// import org.hibernate.annotations.OnDelete;
// import org.hibernate.annotations.OnDeleteAction;
//
// import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.MapsId;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
//
// @Entity
// @Table
// public class TempVoiceChannelCreatorRole {
//     @Id
//     private long id;
//
//     @OneToOne
//     @MapsId
//     @JoinColumn(name = "guild_id")
//     @OnDelete(action = OnDeleteAction.CASCADE)
//     private GuildInfo guild;
//
//     private long tempVoiceChannelCreatorId;
// }
