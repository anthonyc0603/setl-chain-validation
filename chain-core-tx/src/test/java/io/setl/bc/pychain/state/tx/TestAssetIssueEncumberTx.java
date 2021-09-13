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
package io.setl.bc.pychain.state.tx;

import static org.junit.Assert.*;

import io.setl.bc.pychain.accumulator.JSONHashAccumulator;
import io.setl.bc.pychain.common.EncumbranceDetail;
import io.setl.bc.pychain.msgpack.MPWrappedArrayImpl;
import io.setl.common.CommonPy.TxGeneralFields;
import io.setl.common.CommonPy.TxType;
import io.setl.crypto.SHA256;
import java.util.Arrays;
import io.setl.common.Hex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAssetIssueEncumberTx {

  @Before
  public void setUp() throws Exception {
  }


  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test that a serialise/deserialised pair.
   */
  @Test
  public void testSerialiseDeserialise() throws Exception {

    final String fromPubKey = "fromPubKey";
    final String fromAddress = "fromAddress";
    final String nameSpace = "namespace";
    final String classId = "classId";
    final String toAddress = "toAddress";
    final String reference = "reference";
    final String signature = "signature";
    final String protocol = "protocol";
    final String meta = "meta";
    final String poa = "poa";
    final String proto = "proto";
    String hash = "HASH";

    EncumbranceDetail ed1 = new EncumbranceDetail("Address1", 0L, 1000L);
    EncumbranceDetail ed2 = new EncumbranceDetail("Address2", 10L, 2000L);
 
    Object[] beneficiaries = new Object[]{ ed1.encode() };
    Object[] administrators = new Object[]{ ed2.encode() };

    AssetIssueEncumberTx tx = new AssetIssueEncumberTx(1, hash, 3, true, fromPubKey, fromAddress, nameSpace,
        classId, toAddress, 4, reference, beneficiaries, administrators, signature, 5, poa, 6, proto, meta);

    hash = Hex.encode(SHA256.sha256(tx.buildHash(new JSONHashAccumulator()).getBytes()));
    assertTrue(hash.equals("e450cde98064bbfbb28f89d9a428380acdc8b202491c218a5bfd8a99f6123810"));
    tx.setHash(hash);

    assertTrue(tx.getTxType().equals(TxType.ISSUE_AND_ENCUMBER_ASSET));
    assertTrue(((AbstractTx)tx).getTxType().equals(TxType.ISSUE_AND_ENCUMBER_ASSET));
    assertTrue(!tx.getTxType().equals(TxType.ADD_X_CHAIN));

    Object[] encoded = tx.encodeTx();
    AssetIssueEncumberTx tx2 = AssetIssueEncumberTx.decodeTX(encoded);
    AssetIssueEncumberTx tx3 = new AssetIssueEncumberTx(tx);
    AssetIssueEncumberTx tx4 = (AssetIssueEncumberTx)TxFromList.txFromList(new MPWrappedArrayImpl(encoded));

    Object[] encodedBad = tx.encodeTx();
    encodedBad[TxGeneralFields.TX_TXTYPE] = -1;
    assertNull(AssetIssueEncumberTx.decodeTX( new MPWrappedArrayImpl( encodedBad)));

    tx.toString();  // Just for code coverage. This method is just for display purposes at the moment.

    for (AssetIssueEncumberTx thisTx : Arrays.asList(tx, tx2, tx3, tx4)) {

      assertTrue(thisTx.addresses().containsAll(tx.addresses()));
      assertTrue(thisTx.addresses().size() == tx.addresses().size());
      assertTrue(thisTx.getPriority() == TxType.ISSUE_AND_ENCUMBER_ASSET.getPriority());

      assertEquals("chainId does not match", 1, thisTx.getChainId());
      assertEquals("tochainId does not match", 1, thisTx.getToChainId());
      assertEquals("hash does not match", thisTx.getHash(), hash);
      assertEquals("nonce does not match", 3, thisTx.getNonce());
      assertEquals("updated does not match", true, thisTx.isGood());
      assertEquals("fromPublicKey does not match", thisTx.getFromPublicKey(), fromPubKey);
      assertEquals("fromAddress does not match", thisTx.getFromAddress(), fromAddress);
      assertEquals("namespace does not match", thisTx.getNameSpace(), nameSpace);
      assertEquals("classId does not match", thisTx.getClassId(), classId);
      assertEquals("toAddress does not match", thisTx.getToAddress(), toAddress);
      assertEquals("signature does not match", thisTx.getSignature(), signature);
      assertEquals("poa does not match", thisTx.getPowerOfAttorney(), poa);
      assertEquals("timestamp does not match", 6, thisTx.getTimestamp());
      assertEquals("protocol does not match", thisTx.getProtocol(), proto);
      assertEquals("meta does not match", thisTx.getMetadata(), meta);

      Object testEncumber = new EncumbranceDetail((Object [])(((Object [])(thisTx.getEncumbrance().toMap().get("administrators")))[0]));
      assertTrue(testEncumber.equals(ed2));
      testEncumber = new EncumbranceDetail((Object [])(((Object [])(thisTx.getEncumbrance().toMap().get("beneficiaries")))[0]));
      assertTrue(testEncumber.equals(ed1));
      assertEquals(thisTx.getEncumbrance().toMap().get("reference"), reference);
    }
  }

}