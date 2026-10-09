package edu.tianjinagriculture.yunchunapp.VO;

import edu.tianjinagriculture.yunchunapp.tool.YouRenYunDataSimple;
import lombok.Data;

import java.util.List;

@Data
public class YouRenYunDataVO {
    private String f;
    private List<YouRenYunDataSimple> d;
    private String e;
}