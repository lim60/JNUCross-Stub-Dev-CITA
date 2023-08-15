package jnucross.stub.cita.util;

import com.citahub.cita.protocol.core.methods.response.AppBlock;
import com.webank.wecross.stub.Block;
import com.webank.wecross.stub.BlockHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InternalBlock implements Serializable {
    public long blockNumber;
    public String hash;
    public long timeStamp;
    public List<String> transactionsHashes = new ArrayList<>();

    public InternalBlock(AppBlock appBlock) {
        this.blockNumber = appBlock.getBlock().getHeader().getNumberDec().longValue();
        this.hash = appBlock.getBlock().getHash();
        this.timeStamp = appBlock.getBlock().getHeader().getTimestamp();
        List<AppBlock.TransactionObject> transactionObjects = appBlock.getBlock().getBody().getTransactions();
        for (AppBlock.TransactionObject object : transactionObjects){
            transactionsHashes.add(object.getHash());
        }
    }

    public Block toBlock() {
        Block block = new Block();
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setNumber(blockNumber);
        blockHeader.setHash(hash);
        // todo 完善字段

        block.setTransactionsHashes(transactionsHashes);
        block.setBlockHeader(blockHeader);
        block.setRawBytes(new byte[] {}); // raw data
        return block;
    }
}
