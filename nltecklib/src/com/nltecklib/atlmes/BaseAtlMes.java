package com.nltecklib.atlmes;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.atlmes.Root;
import com.nltecklib.protocol.atlmes.mes.A011;
import com.nltecklib.protocol.atlmes.mes.A013.ProductsItem;
import com.nltecklib.protocol.atlmes.mes.A014.ResponseInfo;
import com.nltecklib.protocol.atlmes.mes.A019.ChildEQItem;
import com.nltecklib.protocol.atlmes.mes.A021.SpartLifeTimeInfoItem;
import com.nltecklib.protocol.atlmes.mes.A023.ResourceRecordInfoItem;
import com.nltecklib.protocol.atlmes.mes.A025.ResourceAlertInfoItem;
import com.nltecklib.protocol.atlmes.mes.A033.CellsItem;
import com.nltecklib.protocol.atlmes.mes.A033.RawDataItem;
import com.nltecklib.protocol.atlmes.mes.A034;
import com.nltecklib.protocol.atlmes.mes.A035.DataListItem;
import com.nltecklib.protocol.atlmes.mes.A037.SerialNo;
import com.nltecklib.protocol.atlmes.mes.A038;
import com.nltecklib.protocol.atlmes.mes.A045.EquParamItem;
import com.nltecklib.protocol.atlmes.mes.A049.MaterialInfoItem;
import com.nltecklib.protocol.atlmes.mes.A055.ParamInfoItem;
import com.nltecklib.protocol.atlmes.mes.A082;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.EQStateCode;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.FeedType;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.HLFlag;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.ProcessType;
import com.nltecklib.protocol.atlmes.mes.UserInfo;

/**
 * MES2.0接口
 * 
 * @author guofang_ma
 *
 */
public interface BaseAtlMes {
	/**
	 * 注册监听器，监听MES命令
	 * 
	 * @param listener
	 */
	void addMesListener(MesListener listener);

	/**
	 * 是否连接
	 * 
	 * @return
	 */
	boolean isConnected();

	/**
	 * 连接MES
	 * 
	 * @return
	 * @throws Exception
	 */
	void connectMes() throws IOException;

	/**
	 * 获取MES ip
	 * 
	 * @return
	 */
	String getIp();

	/**
	 * 设置MES ip
	 * 
	 * @param ip
	 */
	void setIp(String ip);

	/**
	 * 获取MES port
	 * 
	 * @return
	 */
	int getPort();

	/**
	 * 设置MES 端口
	 * 
	 * @param ip
	 */
	void setPort(int port);

	/**
	 * 发送回复
	 * 
	 * @param root
	 * @throws IOException
	 */
	void send(Root root) throws IOException;

	/**
	 * 设备信息请求
	 * 
	 * @param pcName
	 * @param EQCode
	 * @throws Exception
	 */
	void a001() throws Exception;

	/**
	 * 单次心跳
	 * 
	 * @return 当前IP地址
	 * @throws Exception
	 */
	void a005() throws Exception;

	/**
	 * 发送设备状态数据 A019
	 * 
	 * @param parentEQStateCode
	 * @param andonState
	 * @param quantity
	 *            A019指令中上传的Quantity的值为设备自投产以来生产的产品总数量--（设备中无法被人工操作清零）
	 * @param childEQ
	 * @param eqCode
	 *            容量机为设备名，如Z99，其他填null
	 * @throws Exception
	 */
	void a019(EQStateCode parentEQStateCode, String andonState, int quantity, List<ChildEQItem> childEQ, String eqCode)
			throws Exception;

	/**
	 * 发送关键件数据
	 * 
	 * @param spartLifeTimeInfo
	 *            关键件信息
	 * @throws Exception
	 */
	void a021(List<SpartLifeTimeInfoItem> spartLifeTimeInfo) throws Exception;

	/**
	 * 发送履历
	 * 
	 * @param resourceRecordInfo
	 *            履历信息
	 * @throws Exception
	 */
	void a023(List<ResourceRecordInfoItem> resourceRecordInfo) throws Exception;

	/**
	 * ACT上传Output数据
	 * 
	 * @param fileName
	 *            对应D盘备份文件名，也对应ScheduleTable.DBF FINAME,例 Z99I010A231413
	 * @param machineNo
	 *            对应ScheduleTable.DBF MACHINEID,例 Z99
	 * @param zoneNo
	 *            对应ScheduleTable.DBF
	 * @param path
	 *            对应ScheduleTable.DBF
	 * @param startTime
	 *            对应ScheduleTable.DBF
	 * @param endTime
	 *            对应ScheduleTable.DBF
	 * @param overFlag
	 *            对应ScheduleTable.DBF
	 * @param schedule
	 *            对应ScheduleTable.DBF
	 * @param testName
	 *            对应ScheduleTable.DBF
	 * @param miPackAgeNo
	 *            电芯条码+号后面的前三位，也可以取流程文件夹MI-A12后三位，即A12
	 * @param testType
	 *            IC/AG
	 * @param cellCountTest
	 *            本次A033上传的电芯个数，按照原本一个备份文件的电芯数
	 * @param cells
	 *            本次上传的电芯，按照原本一个备份文件的电芯
	 * @param rawData
	 *            本次上传的电芯数据，按照原本一个备份文件的电芯数据
	 * @param eqCode
	 *            容量机为设备名，如Z99，其他填null
	 * @param timeout
	 *            上传单包超时时间,单位s
	 * @return
	 * @throws Exception
	 */
	A034.ResponseInfo a033(String fileName, String machineNo, int deviceNo, int zoneNo, String path, Date startTime,
			Date endTime, int overFlag, String schedule, String testName, String miPackAgeNo, String testType,
			int cellCountTest, List<CellsItem> cells, List<RawDataItem> rawData, String eqCode, int timeout)
			throws Exception;

	/**
	 * 上料验证请求
	 * 
	 * @param materialID
	 * @param highOrLowFlag
	 * @param type
	 * @param operation
	 * @return
	 * @throws Exception
	 */
	boolean a015(String materialID, HLFlag highOrLowFlag, FeedType type, ProcessType operation) throws Exception;

	/**
	 * 发送设备报警数据
	 * 
	 * @param resourceAlertInfo
	 * @param eqCode
	 *            容量机为设备名，如Z99，其他填null
	 * @throws Exception
	 */
	void a025(List<ResourceAlertInfoItem> resourceAlertInfo,String eqCode) throws Exception;

	/**
	 * 设备output数据上传请求
	 * 
	 * @param type
	 * @param products
	 * @return
	 * @throws Exception
	 */
	ResponseInfo a013(String type, List<ProductsItem> products) throws Exception;

	/**
	 * 设备运行设定参数更改上传请求
	 * 
	 * @param userInfo
	 * @param equParam
	 * @throws Exception
	 */
	void a045(UserInfo userInfo, List<EquParamItem> equParam) throws Exception;

	/**
	 * 发送单个Tab号和电芯号
	 * 
	 * @param tab
	 * @param cell
	 * @throws Exception
	 */
	void a029(String tab, String cell) throws Exception;

	/**
	 * 真空baking设备与freebaking设备OUTPUT数据专用
	 * 
	 * @param paramInfo
	 * @throws Exception
	 */
	void a055(List<ParamInfoItem> paramInfo) throws Exception;

	/**
	 * 涂布、冷压、分条上传Output数据
	 * 
	 * @param model
	 * @param equType
	 * @param productSN
	 * @param dataList
	 * @throws Exception
	 */
	void a035(String model, String equType, String productSN, List<DataListItem> dataList) throws Exception;

	/**
	 * ACT上传整炉电芯号以获取条码
	 * 
	 * @param cells
	 *            电芯条码
	 * @param eqCode
	 *            容量机：设备名称，例如Z991，其他null
	 * @return FolderPath
	 * @throws Exception
	 */
	String a051(List<String> cells, String eqCode) throws Exception;

	/**
	 * 发送弹夹号和对应各个电芯号
	 * 
	 * @param carrier
	 * @param cell
	 * @throws Exception
	 */
	void a031(String carrier, List<String> cell) throws Exception;

	/**
	 * 设备在MES宕机后，进行清尾生产时产生的电芯码及物料号进行绑定 上传离线生产的电芯条码
	 * 
	 * @param modelInfo
	 * @param productSN
	 * @param userInfo
	 * @param materialInfo
	 * @throws Exception
	 */
	void a049(String modelInfo, List<String> productSN, UserInfo userInfo, List<MaterialInfoItem> materialInfo)
			throws Exception;

	/**
	 * 查询电芯记录
	 * 
	 * @param operationCode
	 *            填 ""
	 * @param reworkFlag
	 *            是否复测
	 * @param serialNoInfo
	 *            barcode
	 * @param eqCode
	 *            容量机为设备号，如Z99，其他为null
	 * @return
	 * @throws Exception
	 */
	A038.ResponseInfo a037(String operationCode, boolean reworkFlag, List<SerialNo> serialNoInfo, String eqCode)
			throws Exception;

	/**
	 * 用户信息请求
	 * 
	 * @param userID
	 * @param userPassword
	 * @return
	 * @throws Exception
	 */
	UserInfo a039(String userID, String userPassword) throws Exception;

	/**
	 * mini PIEF 上料条码获取
	 * 
	 * @param machine
	 *            设备名称，例如Z991
	 * @param is_export
	 *            true即可
	 * @param eqCode
	 *            容量机和machine一致，其他为null
	 * @return
	 * @throws Exception
	 */
	A082.ResponseInfo a081(String machine, boolean is_export, String eqCode, int timeout) throws Exception;

	/**
	 * 回复A012指令
	 * 
	 * @param a011
	 *            为了提供sessionID
	 * @param isOK
	 *            操作成功
	 * @throws Exception
	 */
	void replyA012(A011 a011, boolean isOK) throws Exception;

}
