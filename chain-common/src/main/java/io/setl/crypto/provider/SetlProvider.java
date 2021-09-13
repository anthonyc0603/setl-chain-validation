/* <notice>
 
    SETL Blockchain
    Copyright (C) 2021 SETL Ltd
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License, version 3, as
    published by the Free Software Foundation.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.
 
    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 
</notice> */
package io.setl.crypto.provider;

import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prng.SecureRandomBuilder;
import prng.SecureRandomBuilder.Hash;
import prng.SecureRandomBuilder.Source;
import prng.SecureRandomProvider;

import io.setl.ed25519.DonnaJNI;

/**
 * A provider that can invoke the native C25519 sig
 *
 * @author Simon Greatrix on 2019-08-01.
 */
public class SetlProvider extends Provider {

  /** Original name for Ed25519, before creation of Ed448. */
  public static final String ALGORITHM_1 = "EdDSA";

  /** Current name for Ed25519, to distinguish from Ed448. */
  public static final String ALGORITHM_2 = "Ed25519";

  /** Name for this provider. */
  public static final String NAME = "SETL-Ed25519";

  /** ED-25519 ASN Object Identifier. */
  public static final String OID = EdECObjectIdentifiers.id_Ed25519.toString();

  private static final boolean NATIVE_MODE;

  private static final Logger logger = LoggerFactory.getLogger(SetlProvider.class);

  /**
   * This is a "strong" secure PRNG.
   *
   * <p>
   * This is the NIST PRNG algorithm "HMAC_DRBG" as defined in NIST SP800-90Ar1. The HMAC uses SHA-512 as the digest algorithm. It is seeded with additional
   * entropy on every request. The initial nonce, personalization string and entropy are all generated by the library. The entropy pool is managed using the
   * Fortuna algorithm.
   * </p>
   */
  private static final SecureRandom secureRandom = SecureRandomBuilder.hmac()
      .source(Source.FORTUNA)
      .hash(Hash.SHA512)
      .laziness(0)
      .nonce(null)
      .personalization(null)
      .entropy(null)
      .build();


  static void checkNativeMode() throws NativeModeUnavailableException {
    if (!NATIVE_MODE) {
      throw new NativeModeUnavailableException();
    }
  }


  public static SecureRandom getSecureRandom() {
    return secureRandom;
  }


  /**
   * Ensures that our required security providers are installed in the correct order.
   */
  public static void install() {
    // We require that this provider, secure random provider, and bouncy castle are all installed, with the correct precedence order.
    int myPrecedence = -1;
    int bcPrecedence = -1;
    int prngPrecedence = -1;
    Provider[] allProviders = Security.getProviders();
    for (int i = 0; i < allProviders.length; i++) {
      String n = allProviders[i].getName();
      if (NAME.equals(n)) {
        myPrecedence = i;
      }
      if (BouncyCastleProvider.PROVIDER_NAME.equals(n)) {
        bcPrecedence = i;
      }
      if (SecureRandomProvider.NAME.equals(n)) {
        prngPrecedence = i;
      }
    }

    if (NATIVE_MODE) {
      if (prngPrecedence != -1 && prngPrecedence < myPrecedence && myPrecedence < bcPrecedence) {
        // this is OK, probably. If a provider supporting EdDSA comes before this, then it is not.
        return;
      }
    } else {
      // If no native mode, should not be present
      if (myPrecedence != -1) {
        Security.removeProvider(NAME);
        if (bcPrecedence > myPrecedence) {
          bcPrecedence--;
        }
        if (prngPrecedence > myPrecedence) {
          myPrecedence--;
        }
      }
      if (prngPrecedence != -1 && prngPrecedence < bcPrecedence) {
        // this is OK
        return;
      }
    }

    // Remove and re-add, rather than try to work out the minimal change.
    Security.removeProvider(SecureRandomProvider.NAME);
    Security.removeProvider(NAME);
    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    Security.removeProvider(BouncyCastleJsseProvider.PROVIDER_NAME);

    Security.insertProviderAt(new BouncyCastleJsseProvider(), 1);
    Security.insertProviderAt(new BouncyCastleProvider(), 1);
    if (NATIVE_MODE) {
      Security.insertProviderAt(new SetlProvider(), 1);
    }
    SecureRandomProvider.install(true);
  }


  public static boolean isNativeMode() {
    return NATIVE_MODE;
  }


  static {
    boolean nativeModeA = false;

    try {
      // Test for existence of the donna library.
      new DonnaJNI();
      nativeModeA = true;
      logger.warn("NATIVE DONNA FOUND");
    } catch (UnsatisfiedLinkError | NoClassDefFoundError e1) {
      logger.warn("Native donna library not found");
    }

    NATIVE_MODE = nativeModeA;
  }


  /**
   * Create the provider.
   */
  public SetlProvider() {
    super(NAME, "1.0", "Support for native Ed25519 calculations");
    put("Signature." + ALGORITHM_1, EdDsaSignature.class.getName());
    put("Signature." + ALGORITHM_2, EdDsaSignature.class.getName());
    put("Signature." + OID, EdDsaSignature.class.getName());
    put("Signature.OID." + OID, EdDsaSignature.class.getName());
    put("KeyFactory." + ALGORITHM_1, EdDsaKeyFactory.class.getName());
    put("KeyFactory." + ALGORITHM_2, EdDsaKeyFactory.class.getName());
    put("KeyFactory." + OID, EdDsaKeyFactory.class.getName());
    put("KeyFactory.OID." + OID, EdDsaKeyFactory.class.getName());
    put("KeyPairGenerator." + ALGORITHM_1, EdDsaKeyPairGenerator.class.getName());
    put("KeyPairGenerator." + ALGORITHM_2, EdDsaKeyPairGenerator.class.getName());
    put("KeyPairGenerator." + OID, EdDsaKeyPairGenerator.class.getName());
    put("KeyPairGenerator.OID." + OID, EdDsaKeyPairGenerator.class.getName());
  }

}