package com.muzlik.pvpcombat.integration.crossserver;

import java.io.*;
import java.util.UUID;

/**
 * Network packet for cross-server combat synchronization.
 * Handles serialization/deserialization for plugin messaging channels.
 */
public class SyncPacket {
    private final String channel;
    private final PacketType type;
    private final CombatSyncData data;

    public enum PacketType {
        COMBAT_START,
        COMBAT_END,
        COMBAT_UPDATE,
        BROADCAST_MESSAGE
    }

    public SyncPacket(String channel, PacketType type, CombatSyncData data) {
        this.channel = channel;
        this.type = type;
        this.data = data;
    }

    public String getChannel() { return channel; }
    public PacketType getType() { return type; }
    public CombatSyncData getData() { return data; }

    /**
     * Serializes the packet to a byte array for network transmission.
     */
    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            // Write channel
            dos.writeUTF(channel);

            // Write packet type
            dos.writeUTF(type.name());

            // Write combat data
            dos.writeUTF(data.getSessionId().toString());
            dos.writeUTF(data.getAttackerId().toString());
            dos.writeUTF(data.getDefenderId().toString());
            dos.writeUTF(data.getAttackerName());
            dos.writeUTF(data.getDefenderName());
            dos.writeUTF(data.getServerName());
            dos.writeLong(data.getStartTime());
            dos.writeInt(data.getRemainingSeconds());
            dos.writeBoolean(data.isActive());

            return baos.toByteArray();
        }
    }

    /**
     * Deserializes a packet from a byte array.
     */
    public static SyncPacket deserialize(byte[] data) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             DataInputStream dis = new DataInputStream(bais)) {

            // Read channel
            String channel = dis.readUTF();

            // Read packet type
            PacketType type = PacketType.valueOf(dis.readUTF());

            // Read combat data
            UUID sessionId = UUID.fromString(dis.readUTF());
            UUID attackerId = UUID.fromString(dis.readUTF());
            UUID defenderId = UUID.fromString(dis.readUTF());
            String attackerName = dis.readUTF();
            String defenderName = dis.readUTF();
            String serverName = dis.readUTF();
            long startTime = dis.readLong();
            int remainingSeconds = dis.readInt();
            boolean active = dis.readBoolean();

            CombatSyncData syncData = new CombatSyncData(sessionId, attackerId, defenderId,
                                                        attackerName, defenderName, serverName,
                                                        startTime, remainingSeconds, active);

            return new SyncPacket(channel, type, syncData);
        }
    }

    @Override
    public String toString() {
        return String.format("SyncPacket{channel='%s', type=%s, data=%s}",
                           channel, type, data);
    }
}