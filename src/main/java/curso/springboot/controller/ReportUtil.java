package curso.springboot.controller;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component //do Spring
public class ReportUtil implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//retorna o PDF em byte para Download no navegador
	public byte[] geraRelatorio(List listDados,
								String relatorio, ServletContext servletContext )
								 throws Exception{
		
		/*Cria a lista de dados para o relatório com nossa lista de objetos para imprimir*/
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);
		
		/*Carrega o caminho do arquivo jasper compilado*/
		String caminhoJasper = servletContext.getRealPath("relatorios")
				+ File.separator + relatorio + ".jasper";
		
		/*Carrega o arquivo jasper passando os dados*/
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashedMap(), jrbcds);
		
		/*Exporta para byte para fazer o download do PDF*/
		return JasperExportManager.exportReportToPdf(impressoraJasper);
		
	}

}
