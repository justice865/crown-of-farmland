package edu.kit.kastel.model;

/**
 * The UnitType record represents the properties of a unit related to its card attributes.
 * @param qualifier first part of the unit's name
 * @param role second part of the unit's name
 * @param attack the attack value of the unit
 * @param defense the defense value of the unit
 * @author uqibg
 */
public record UnitType(String qualifier, String role, int attack, int defense) {
    /**
     * Determines whether this unit type can be merged with another unit type based on specific rules.
     * @param other the other unit type to compare with
     * @return true if the unit types can be merged according to the defined rules, false otherwise
     */
    public boolean canMergeWith(UnitType other) {
        if (this.qualifier.equals(other.qualifier) && this.role.equals(other.role)) {
            return false;
        }
        if (this.attack > other.attack && this.attack == other.defense && this.defense == other.attack) {
            return true;
        }
        int gcdAttack = gcd(this.attack, other.attack);
        int gcdDefense = gcd(this.defense, other.defense);
        int g3t = Math.max(gcdAttack, gcdDefense);

        if (g3t > 100) {
            return true;
        }
        return g3t == 100 && ((isPrime(this.attack / 100) && isPrime(other.attack / 100))
                || (isPrime(this.defense / 100) && isPrime(other.defense / 100)));
    }

    /**
     * Merges this unit type with another unit type according to specific rules, resulting in a new unit type.
     * @param other the other unit type to merge with
     * @return a new UnitType that is the result of merging this unit type with the other unit type according to the defined rules
     *         ,or this unit type if the merge cannot be performed
     */
    public UnitType mergeWith(UnitType other) {
        int gcdAttack = gcd(this.attack, other.attack);
        int gcdDefense = gcd(this.defense, other.defense);
        int g3t = Math.max(gcdAttack, gcdDefense);
        String mergedQualifier = other.qualifier + " " + this.qualifier;
        if (this.qualifier.equals(other.qualifier) && this.role.equals(other.role)) {
            return this;

        }
        if (this.attack > other.attack && this.attack == other.defense && this.defense == other.attack) {
            return new UnitType(mergedQualifier, other.role, this.attack, other.defense);
        } else if (g3t > 100) {
            int conspirativeAttack = this.attack + other.attack - g3t;
            int conspirativeDefense = this.defense + other.defense - g3t;
            return new UnitType(mergedQualifier, other.role, conspirativeAttack, conspirativeDefense);
            
        } else if (g3t == 100 && (isPrime(this.attack / 100) && isPrime(other.attack / 100))
                || (isPrime(this.defense / 100) && isPrime(other.defense / 100))) {
            int primeAttack = this.attack + other.attack;
            int primeDefense = this.defense + other.defense;
            return new UnitType(mergedQualifier, other.role, primeAttack, primeDefense);
        } else {
            return this;
        }

    }
    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

}