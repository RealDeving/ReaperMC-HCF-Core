package me.rainny.reaper.factionutils.claim;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.doctordark.utils.GenericUtils;
import com.doctordark.utils.other.cuboid.Cuboid;
import com.doctordark.utils.other.cuboid.NamedCuboid;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.type.ClaimableFaction;
import me.rainny.reaper.factionutils.type.Faction;

import java.util.*;

public class Claim extends NamedCuboid implements Cloneable, ConfigurationSerializable {
	private static final Random RANDOM = new Random();
	private final Map<String, Subclaim> subclaims = new CaseInsensitiveMap<String, Subclaim>();
	private final UUID claimUniqueID;
	private final UUID factionUUID;
	private Faction faction;
	private boolean loaded = false;

	public Claim(Map<String, Object> map) {
		super(map);
		this.name = ((String) map.get("name"));
		this.claimUniqueID = UUID.fromString((String) map.get("claimUUID"));
		this.factionUUID = UUID.fromString((String) map.get("factionUUID"));
		Iterator<Subclaim> var2 = GenericUtils.createList(map.get("subclaims"), Subclaim.class).iterator();
		while (var2.hasNext()) {
			Subclaim subclaim = (Subclaim) var2.next();
			this.subclaims.put(subclaim.getName(), subclaim);
		}
	}

	public Claim(Faction faction, Location location) {
		super(location, location);
		this.name = generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, Location location1, Location location2) {
		super(location1, location2);
		this.name = generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		super(world, x1, y1, z1, x2, y2, z2);
		this.name = generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, Cuboid cuboid) {
		super(cuboid);
		this.name = generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("name", this.name);
		map.put("claimUUID", this.claimUniqueID.toString());
		map.put("factionUUID", this.factionUUID.toString());
		map.put("subclaims", new ArrayList<Subclaim>(this.subclaims.values()));
		return map;
	}

	private String generateName() {
		return String.valueOf(RANDOM.nextInt(899) + 100);
	}

	public UUID getClaimUniqueID() {
		return this.claimUniqueID;
	}

	public ClaimableFaction getFaction() {
		if ((!this.loaded) && (this.faction == null)) {
			this.faction = HCF.getPlugin().getFactionManager().getFaction(this.factionUUID);
			this.loaded = true;
		}
		return (this.faction instanceof ClaimableFaction) ? (ClaimableFaction) this.faction : null;
	}

	public Collection<Subclaim> getSubclaims() {
		return this.subclaims.values();
	}

	public Subclaim getSubclaim(String name) {
		return (Subclaim) this.subclaims.get(name);
	}

	public String getFormattedName() {
		return getName() + ": (" + this.worldName + ", " + this.x1 + ", " + this.y1 + ", " + this.z1 + ") - ("
				+ this.worldName + ", " + this.x2 + ", " + this.y2 + ", " + this.z2 + ')';
	}

	public Claim clone() {
		return (Claim) super.clone();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		Claim blocks = (Claim) o;
		if (this.loaded != blocks.loaded) {
			return false;
		}
		if (this.subclaims != null ? !this.subclaims.equals(blocks.subclaims) :

				blocks.subclaims != null) {
			return false;
		}
		if (this.claimUniqueID != null ? !this.claimUniqueID.equals(blocks.claimUniqueID) :

				blocks.claimUniqueID != null) {
			return false;
		}
		if (this.factionUUID != null ? !this.factionUUID.equals(blocks.factionUUID) :

				blocks.factionUUID != null) {
			return false;
		}
		if (this.faction != null) {
			if (!this.faction.equals(blocks.faction)) {
				return false;
			}
		} else if (blocks.faction != null) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result = this.subclaims != null ? this.subclaims.hashCode() : 0;
		result = 31 * result + (this.claimUniqueID != null ? this.claimUniqueID.hashCode() : 0);
		result = 31 * result + (this.factionUUID != null ? this.factionUUID.hashCode() : 0);
		result = 31 * result + (this.faction != null ? this.faction.hashCode() : 0);
		result = 31 * result + (this.loaded ? 1 : 0);
		return result;
	}
}
