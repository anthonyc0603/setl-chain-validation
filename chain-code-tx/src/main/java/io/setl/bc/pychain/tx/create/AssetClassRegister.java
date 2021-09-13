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
package io.setl.bc.pychain.tx.create;

import static io.setl.common.StringUtils.cleanString;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Strings;
import io.setl.bc.pychain.state.tx.AssetClassRegisterTx;
import io.setl.bc.pychain.state.tx.Hash;
import io.setl.common.CommonPy.TxExternalNames;
import io.setl.common.CommonPy.TxType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(name = TxExternalNames.REGISTER_ASSET_CLASS, description = "Register an asset class in a name space.", allOf = BaseTransaction.class)
@JsonDeserialize
public class AssetClassRegister extends BaseAssetClassRegister {

  /**
   * assetClassRegisterUnsigned().
   * <p>Create an unsigned AssetClassRegisterTx</p>
   *
   * @param chainID     :
   * @param nonce       :
   * @param fromPubKey  :
   * @param fromAddress :
   * @param nameSpace   :
   * @param classId     :
   * @param metadata    :
   * @param poa         :
   *
   * @return :
   */
  @SuppressWarnings("squid:S00107") // Params > 7
  public static AssetClassRegisterTx assetClassRegisterUnsigned(
      int chainID,
      long nonce,
      String fromPubKey,
      String fromAddress,
      String nameSpace,
      String classId,
      String metadata,
      String poa
  ) {

    AssetClassRegisterTx rVal = new AssetClassRegisterTx(
        chainID,
        4,
        "",
        nonce,
        false,
        fromPubKey,
        fromAddress,
        cleanString(nameSpace),
        cleanString(classId),
        metadata,
        "",
        -1,
        poa,
        Instant.now().getEpochSecond()
    );

    rVal.setHash(Hash.computeHash(rVal));

    return rVal;
  }


  public AssetClassRegister() {
  }


  /**
   * Recreate the specification of the transaction.
   *
   * @param tx the transaction
   */
  public AssetClassRegister(AssetClassRegisterTx tx) {
    super(tx);
  }


  @Override
  public AssetClassRegisterTx create() {
    AssetClassRegisterTx rVal = new AssetClassRegisterTx(
        getChainId(),
        4,
        getHash(),
        getNonce(),
        isUpdated(),
        getPublicKey(),
        getAddress(),
        getNameSpace(),
        getClassId(),
        getMetadata(),
        getSignature(),
        getHeight(),
        getPoa(),
        getTimestamp()
    );

    if (Strings.isNullOrEmpty(getHash())) {
      rVal.setHash(Hash.computeHash(rVal));
    }

    return rVal;
  }


  @Override
  public TxType getTxType() {
    return TxType.REGISTER_ASSET_CLASS;
  }
}
