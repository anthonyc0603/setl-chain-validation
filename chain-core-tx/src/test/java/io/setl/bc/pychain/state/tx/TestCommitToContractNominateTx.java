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

import static io.setl.common.CommonPy.ContractConstants.CONTRACT_NAME_NOMINATE;
import static io.setl.common.CommonPy.ContractConstants.CONTRACT_NAME_TOKENS_NOMINATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.setl.bc.pychain.accumulator.JSONHashAccumulator;
import io.setl.bc.pychain.msgpack.MPWrappedArrayImpl;
import io.setl.bc.pychain.msgpack.MPWrappedMap;
import io.setl.bc.pychain.state.tx.contractdataclasses.IContractData;
import io.setl.common.CommonPy.TxGeneralFields;
import io.setl.common.CommonPy.TxType;
import io.setl.crypto.SHA256;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import io.setl.common.Hex;
import org.junit.Test;

public class TestCommitToContractNominateTx {
  
  /**
   * testStatic.
   */
  @Test
  public void testStatic() {
    
    final Map<String, Object> map = new HashMap<>();
    map.put("__function", CONTRACT_NAME_TOKENS_NOMINATE);
    
    final MPWrappedMap<String, Object> dictionary = new MPWrappedMap<>(map);
    
    IContractData testData1 = CommitToContractTx.getContractDataFromDictionary(dictionary);
    
    assertTrue(testData1.get__function().equals(CONTRACT_NAME_NOMINATE));
  }
  
  @Test
  public void testSerialiseDeserialise() throws Exception {
  
    final String fromPubKey = "fromPubKey";
    final String fromAddress = "fromAddress";
    final String nameSpace = "namespace";
    final String classId = "classId";
    final String toAddress = "toAddress";
    final String signature = "signature";
    final String protocol = "protocol";
    final String meta = "meta";
    final String poa = "poa";
    final String proto = "proto";
    final String contractAddress = "contractAddress";
    String hash = "HASH";
  
    //  create a wrapped map with a single key+value pair
    //
    final Map<String, Object> map = new HashMap<>();
    map.put("__function", CONTRACT_NAME_TOKENS_NOMINATE);
    final MPWrappedMap<String, Object> contractData = new MPWrappedMap<>(map);
  
    CommitToContractTx tx = new CommitToContractTx(1,            //  chainId
        hash,
        3,            //  nonce
        true,         //  updated
        fromPubKey,
        fromAddress,
        contractAddress,
        contractData,
        signature,
        5,            //  height
        poa,
        6);           //  timestamp
  
    hash = Hex.encode(SHA256.sha256(tx.buildHash(new JSONHashAccumulator()).getBytes()));
    assertTrue(hash.equals("0d3b80d60fa71fa8e16970ac9ed78aa611978c2abc8b75913c78ee3c14b5d0bf"));
    tx.setHash(hash);

    assertTrue(tx.getTxType().equals(TxType.COMMIT_TO_CONTRACT));
    assertTrue(((AbstractTx)tx).getTxType().equals(TxType.COMMIT_TO_CONTRACT));
    assertTrue(!tx.getTxType().equals(TxType.ADD_X_CHAIN));

    Object[] encoded = tx.encodeTx();
  
    CommitToContractTx tx2 = CommitToContractTx.decodeTX(encoded);
    CommitToContractTx tx3 = new CommitToContractTx(tx);
    CommitToContractTx tx4 = (CommitToContractTx) TxFromList.txFromList(new MPWrappedArrayImpl(encoded));
    CommitToContractTx tx5 = new CommitToContractTx(tx);
    tx5.setCommitmentDictionary(contractData); //

    Object[] encodedBad = tx.encodeTx();
    encodedBad[TxGeneralFields.TX_TXTYPE] = -1;
    assertNull(CommitToContractTx.decodeTX( new MPWrappedArrayImpl( encodedBad)));

    tx.toString();  // Just for code coverage. This method is just for display purposes at the moment.

    for (CommitToContractTx thisTx : Arrays.asList(tx, tx2, tx3, tx4, tx5)) {

      assertTrue(thisTx.addresses().containsAll(tx.addresses()));
      assertTrue(thisTx.addresses().size() == tx.addresses().size());
      assertTrue(thisTx.getPriority() == TxType.COMMIT_TO_CONTRACT.getPriority());

      assertEquals("chainId does not match", 1, thisTx.getChainId());
      assertEquals("tochainId does not match", 1, thisTx.getToChainId());
      assertEquals("hash does not match", thisTx.getHash(), hash);
      assertEquals("nonce does not match", 3, thisTx.getNonce());
      assertEquals("updated does not match", true, thisTx.isGood());
      assertEquals("fromPublicKey does not match", thisTx.getFromPublicKey(), fromPubKey);
      assertEquals("fromAddress does not match", thisTx.getFromAddress(), fromAddress);
    
      assertEquals("contractAddress does not match", thisTx.getContractAddress().get(0), contractAddress);
    
      MPWrappedMap<String, Object> fred = thisTx.getCommitmentDictionary();
    
      if (thisTx != tx3) {
        int[] count = new int[] {0};
        fred.iterate((k, v) -> {
          count[0]++;
          assertEquals("NO", "__function", k);
          assertEquals("NO", CONTRACT_NAME_TOKENS_NOMINATE, v);
        });
        assertEquals("NO", 1, count[0]);
      } else {
        NavigableMap dvpMap = fred.toMap();
        assertEquals("No", CONTRACT_NAME_NOMINATE, dvpMap.get("__function"));
      }
    
      assertEquals("signature does not match", thisTx.getSignature(), signature);
    
      //  height is unused
      //
      //  assertEquals( "height does not match",        thisTx.getHeight(),          5);
    
      assertEquals("poa does not match", thisTx.getPowerOfAttorney(), poa);
      assertEquals("timestamp does not match", 6, thisTx.getTimestamp());
    }
  }
  
}
