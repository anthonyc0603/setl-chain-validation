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
import io.setl.common.Balance;
import io.setl.common.CommonPy.TxGeneralFields;
import io.setl.common.CommonPy.TxType;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExerciseEncumbranceTx extends AbstractTx implements HasNamespace, HasClassId, HasToAddress, HasAmount, HasProtocol {

  public static final TxType TXT = TxType.EXERCISE_ENCUMBRANCE;

  private static final Logger logger = LoggerFactory.getLogger(ExerciseEncumbranceTx.class);


  /**
   * Take AssetIssueTx and copy construct with a null signature.
   *
   * @param toCopy : The transaction to be stripped of signature
   *
   * @return : The signature-less cloned transaction
   */
  public static ExerciseEncumbranceTx cloneWithoutSignature(ExerciseEncumbranceTx toCopy) {
    return new ExerciseEncumbranceTx(
        toCopy.chainId,
        toCopy.getHash(),
        toCopy.nonce,
        toCopy.updated,
        toCopy.fromPubKey,
        toCopy.fromAddress,
        toCopy.subjectAddress,
        toCopy.nameSpace,
        toCopy.classId,
        toCopy.reference,
        toCopy.toChainId,
        toCopy.toAddress,
        toCopy.amount,
        null,
        toCopy.protocol,
        toCopy.metadata,
        toCopy.height,
        toCopy.powerOfAttorney,
        toCopy.timestamp
    );
  }


  /**
   * Accept an Object[] and return a ExerciseEncumbranceTx.
   *
   * @param encodedTx : The input Object[]
   *
   * @return : The returned ExerciseEncumbranceTx object
   */
  public static ExerciseEncumbranceTx decodeTX(Object[] encodedTx) {
    return decodeTX(new MPWrappedArrayImpl(encodedTx));
  }


  /**
   * Accept MPWrappedArray argument and create ExerciseEncumbranceTx from it.
   *
   * @param txData : The input MPWrappedArray
   *
   * @return : The constructed ExerciseEncumbranceTx object
   */
  public static ExerciseEncumbranceTx decodeTX(MPWrappedArray txData) {
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
    long timestamp = txData.asLong(8);
    String poa = txData.asString(9);
    int toChainId = txData.asInt(10);
    String toAddress = txData.asString(11);
    String subjectAddress = txData.asString(12);
    String nameSpace = txData.asString(13);
    String classId = txData.asString(14);
    Number amount = (new Balance(txData.get(15))).getValue();
    String reference = txData.asString(16);
    String protocol = txData.asString(17);
    String metadata = txData.asString(18);
    String signature = txData.asString(19);
    int height = txData.asInt(20);
    return new ExerciseEncumbranceTx(
        chainId,
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
        timestamp
    );
  }


  protected Number amount;

  protected String classId;

  protected String metadata;

  protected String nameSpace;

  protected String protocol;

  protected String reference;

  protected String subjectAddress;

  protected String toAddress;

  protected int toChainId;


  /**
   * Constructor.
   *
   * @param chainId     : Txn blockchain ID
   * @param hash        : Txn hash
   * @param nonce       : Txn nonce
   * @param updated     : Txn updated true/false
   * @param fromPubKey  : Txn from Public Key
   * @param fromAddress : Txn from address
   * @param nameSpace   : Txn namespace
   * @param classId     : Txn classID
   * @param toChainId   : Txn target blockchain ID
   * @param toAddress   : Txn to address
   * @param reference   : Txn to reference
   * @param amount      : Txn amount
   * @param signature   : Txn signature
   * @param protocol    : Txn protocol
   * @param metadata    : Txn metadata
   * @param height      : Txn height
   * @param poa         : Txn Power of Attorney
   * @param timestamp   : Txn timestamp
   */
  @SuppressWarnings("squid:S00107") // Params > 7
  public ExerciseEncumbranceTx(
      int chainId,
      String hash,
      long nonce,
      boolean updated,
      String fromPubKey,
      String fromAddress,
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
      long timestamp
  ) {
    //  ensure that super class constructor is called
    //
    super(chainId, hash, nonce, updated, fromAddress, fromPubKey, signature, poa, timestamp);
    this.subjectAddress = (subjectAddress == null ? "" : subjectAddress);
    this.nameSpace = (nameSpace == null ? "" : nameSpace);
    this.classId = (classId == null ? "" : classId);
    this.reference = (reference == null ? "" : reference);
    this.toAddress = toAddress;
    this.amount = (new Balance(amount)).getValue();
    this.protocol = (protocol == null ? "" : protocol);
    this.metadata = (metadata == null ? "" : metadata);
    this.height = height;
    this.toChainId = toChainId;
  }


  /**
   * Copy Constructor.
   *
   * @param toCopy :
   */
  public ExerciseEncumbranceTx(ExerciseEncumbranceTx toCopy) {
    //  ensure superclass constructor is called.
    //
    super(toCopy);
    this.nameSpace = toCopy.getNameSpace();
    this.classId = toCopy.getClassId();
    this.toAddress = toCopy.getToAddress();
    this.amount = toCopy.getAmount();
    this.metadata = toCopy.getMetadata();
    this.protocol = toCopy.getProtocol();
    this.height = toCopy.getHeight();
    this.toChainId = toCopy.getToChainId();
    this.reference = toCopy.getReference();
    this.subjectAddress = toCopy.getSubjectAddress();
  }


  /**
   * Return associated addresses for this Transaction.
   *
   * @return :
   */
  @Override
  public Set<String> addresses() {
    Set<String> rVal = new TreeSet<>();
    rVal.add(this.fromAddress);
    if ((this.toAddress != null) && (this.toAddress.length() > 0)) {
      rVal.add(this.toAddress);
    }
    if ((this.subjectAddress != null) && (this.subjectAddress.length() > 0)) {
      rVal.add(this.subjectAddress);
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
    return new Object[]{
        this.chainId,
        this.int1,
        TXT.getId(),
        this.hash,
        this.nonce,
        this.updated,
        this.fromPubKey,
        this.fromAddress,
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


  public Number getAmount() {
    return amount;
  }


  public String getClassId() {
    return classId;
  }


  @Override
  public String getMetadata() {
    return metadata;
  }


  public String getNameSpace() {
    return nameSpace;
  }


  @Override
  public int getPriority() {
    return TXT.getPriority();
  }


  public String getProtocol() {
    return protocol;
  }


  public String getReference() {
    return reference;
  }


  public String getSubjectAddress() {
    return subjectAddress;
  }


  public String getToAddress() {
    return toAddress;
  }


  @Override
  public int getToChainId() {
    return toChainId;
  }


  @Override
  public TxType getTxType() {
    return TXT;
  }


  @Override
  public String toString() {
    return String.format("%s nameSpace:%s classId:%s from:%s to:%s %s", super.toString(), nameSpace, classId, fromAddress, toAddress, amount.toString());
  }
}
