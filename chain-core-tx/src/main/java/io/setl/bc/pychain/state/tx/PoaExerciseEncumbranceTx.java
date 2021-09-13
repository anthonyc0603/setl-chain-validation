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

import io.setl.bc.pychain.accumulator.HashAccumulator;
import io.setl.bc.pychain.msgpack.MPWrappedArray;
import io.setl.bc.pychain.msgpack.MPWrappedArrayImpl;
import io.setl.bc.pychain.state.tx.helper.HasAmount;
import io.setl.bc.pychain.state.tx.helper.HasClassId;
import io.setl.bc.pychain.state.tx.helper.HasNamespace;
import io.setl.bc.pychain.state.tx.helper.HasProtocol;
import io.setl.bc.pychain.state.tx.helper.HasToAddress;
import io.setl.common.CommonPy.TxGeneralFields;
import io.setl.common.CommonPy.TxType;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PoaExerciseEncumbranceTx extends ExerciseEncumbranceTx implements HasNamespace, HasClassId, HasToAddress, HasAmount, HasProtocol {

  public static final TxType TXT = TxType.POA_EXERCISE_ENCUMBRANCE;

  private static final Logger logger = LoggerFactory.getLogger(PoaExerciseEncumbranceTx.class);


  /**
   * Accept an Object[] and return a PoaExerciseEncumbranceTx.
   *
   * @param encodedTx : The input Object[]
   *
   * @return : The returned PoaExerciseEncumbranceTx object
   */
  public static PoaExerciseEncumbranceTx decodeTX(Object[] encodedTx) {

    return decodeTX(new MPWrappedArrayImpl(encodedTx));
  }


  /**
   * Accept MPWrappedArray argument and create PoaExerciseEncumbranceTx from it.
   *
   * @param txData : The input MPWrappedArray
   *
   * @return : The constructed PoaExerciseEncumbranceTx object
   */
  public static PoaExerciseEncumbranceTx decodeTX(MPWrappedArray txData) {

    int chainId = txData.asInt(TxGeneralFields.TX_CHAIN);
    TxType txt = TxType.get(txData.asInt(TxGeneralFields.TX_TXTYPE));

    if (txt != TXT) {
      logger.error("Unsupported:{}", txt);
      return null;
    }

    String hash = txData.asString(TxGeneralFields.TX_HASH);
    long nonce = txData.asLong(TxGeneralFields.TX_NONCE);
    boolean updated = txData.asBoolean(TxGeneralFields.TX_UPDATED);

    String fromPubKey = txData.asString(TxGeneralFields.TX_FROM_PUB);
    String fromAddress = txData.asString(TxGeneralFields.TX_FROM_ADDR);

    String poaAddress = txData.asString(8);
    String poaReference = txData.asString(9);

    long timestamp = txData.asLong(10);
    String poa = txData.asString(11);
    int toChainId = txData.asInt(12);
    String toAddress = txData.asString(13);
    String subjectAddress = txData.asString(14);
    String nameSpace = txData.asString(15);
    String classId = txData.asString(16);
    long amount = txData.asLong(17);
    String reference = txData.asString(18);
    String protocol = txData.asString(19);
    String metadata = txData.asString(20);
    String signature = txData.asString(21);
    int height = txData.asInt(22);

    return new PoaExerciseEncumbranceTx(chainId,
        hash,
        nonce,
        updated,
        fromPubKey,
        fromAddress,
        poaAddress,
        poaReference,
        subjectAddress,
        nameSpace,
        classId,
        reference,
        toChainId,
        toAddress,
        amount,
        signature,
        protocol,
        metadata,
        height,
        poa,
        timestamp
    );

  }

  private String poaAddress;

  private String poaReference;

  /**
   * Constructor.
   *
   * @param chainId        : Txn blockchain ID
   * @param hash           : Txn hash
   * @param nonce          : Txn nonce
   * @param updated        : Txn updated true/false
   * @param fromPubKey     : Txn from Public Key
   * @param fromAddress    : Address Exercising the POA
   * @param poaAddress     : Effective Address Exercising the Encumbrance
   * @param poaReference   : poa Reference
   * @param subjectAddress : Address against which the encumbrance is registered.
   * @param nameSpace      : Txn namespace
   * @param classId        : Txn classID
   * @param toChainId      : Txn target blockchain ID
   * @param toAddress      : Txn to address
   * @param reference      : Txn to reference
   * @param amount         : Txn amount
   * @param signature      : Txn signature
   * @param protocol       : Txn protocol
   * @param metadata       : Txn metadata
   * @param height         : Txn height
   * @param poa            : Txn Power of Attorney
   * @param timestamp      : Txn timestamp
   */
  @SuppressWarnings("squid:S00107") // Params > 7
  public PoaExerciseEncumbranceTx(int chainId,
      String hash,
      long nonce,
      boolean updated,
      String fromPubKey,
      String fromAddress,
      String poaAddress,
      String poaReference,
      String subjectAddress,
      String nameSpace,
      String classId,
      String reference,
      int toChainId,
      String toAddress,
      Number amount,
      String signature,
      String protocol,
      String metadata,
      int height,
      String poa,
      long timestamp) {

    //  ensure that super class constructor is called
    //
    super(chainId,
        hash,
        nonce,
        updated,
        fromPubKey,
        fromAddress,
        subjectAddress,
        nameSpace,
        classId,
        reference,
        toChainId,
        toAddress,
        amount,
        signature,
        protocol,
        metadata,
        height,
        poa,
        timestamp);

    this.poaAddress = (poaAddress == null ? "" : poaAddress);
    this.poaReference = (poaReference == null ? "" : poaReference);
  }


  /**
   * Copy Constructor.
   *
   * @param toCopy :
   */
  public PoaExerciseEncumbranceTx(PoaExerciseEncumbranceTx toCopy) {

    //  ensure superclass constructor is called.
    //
    super(toCopy);

    this.poaAddress = toCopy.getPoaAddress();
    this.poaReference = toCopy.getPoaReference();

  }


  /**
   * Return associated addresses for this Transaction.
   *
   * @return :
   */
  @Override
  public Set<String> addresses() {

    Set<String> rVal = super.addresses();

    if ((this.poaAddress != null) && (!this.poaAddress.isEmpty())) {
      rVal.add(this.poaAddress);
    }

    return rVal;
  }


  @Override
  public HashAccumulator buildHash(HashAccumulator hashList) {

    hashList.addAll(new Object[]{
        this.chainId,
        TXT.getId(),
        this.nonce,
        this.fromPubKey,
        this.fromAddress,
        this.poaAddress,
        this.poaReference,
        this.timestamp,
        this.powerOfAttorney,
        this.toChainId,
        this.toAddress,
        this.subjectAddress,
        this.nameSpace,
        this.classId,
        this.amount,
        this.reference,
        this.protocol,
        this.metadata
    });

    return hashList;
  }


  /**
   * Return this transation as an Object [].
   *
   * @return :   Object []
   */
  @Override
  public Object[] encodeTx() {

    return new Object[]{this.chainId,
        this.int1,
        TXT.getId(),
        this.hash,
        this.nonce,
        this.updated,
        this.fromPubKey,
        this.fromAddress,
        this.poaAddress,
        this.poaReference,
        this.timestamp,
        this.powerOfAttorney,
        this.toChainId,
        this.toAddress,
        this.subjectAddress,
        this.nameSpace,
        this.classId,
        this.amount,
        this.reference,
        this.protocol,
        this.metadata,
        this.signature,
        this.height
    };
  }


  @Override
  public String getPoaAddress() {

    return poaAddress;
  }


  @Override
  public String getPoaReference() {

    return poaReference;
  }


  @Override
  public int getPriority() {

    return TXT.getPriority();
  }


  @Override
  public TxType getTxType() {

    return TXT;
  }


  @Override
  public boolean isPOA() {
    return true;
  }

}
