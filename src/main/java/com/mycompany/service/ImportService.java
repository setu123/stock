package com.mycompany.service;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.model.Item;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.webharvest.definition.ScraperConfiguration;

/**
 * @date May 15, 2015
 * @author Setu
 */
public class ImportService {

    private final String STOCKBANGLADESH_IMPORT_DATE_FORMAT = "dd-MMM-yy";
    private final String DSE_DATA_ARCHIVE_DATE_FORMAT = "yyyy-MM-dd";
    private final String ALL_INSTRUMENT = "All Instrument";
    private DateFormat dateFormat = new SimpleDateFormat(STOCKBANGLADESH_IMPORT_DATE_FORMAT);
    private final ItemDaoImpl dao;
    private ServletContext context = null;

    public ImportService() {
        dao = new ItemDaoImpl();
    }
    
    public ImportService(ItemDaoImpl daoImpl) {
        dao = daoImpl;
    }

    public ImportService(ServletContext context) {
        this.context = context;
        dao = new ItemDaoImpl();
    }

    public void importArchive(String code, int day) {
        try {
            if (day < 1) {
                day = 7;
            }
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_YEAR, -day);
            Calendar endDate = Calendar.getInstance();
            Item item = new Item();
            item.setCode(code);

            List<Crawler> crawlers = new ArrayList<>();
            ScraperConfiguration config = Crawler.getScraperConfig(context, Crawler.CrawlType.DATA_ARCHIVE);
            Map params = new HashMap();
            dateFormat = new SimpleDateFormat(DSE_DATA_ARCHIVE_DATE_FORMAT);
            params.put("startDate", dateFormat.format(startDate.getTime()));
            params.put("endDate", dateFormat.format(endDate.getTime()));
            Crawler crawler = new Crawler(config, item, Crawler.CrawlType.DATA_ARCHIVE, params);
            crawler.start();
            crawlers.add(crawler);
            for (Crawler craw : crawlers) {
                craw.join();
            }

            List<Item> items = (List<Item>) params.get("items");
            dao.open();
            dao.importItems(items);
            dao.close();
        } catch (FileNotFoundException | InterruptedException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ImportService.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    public void importAlphabeticArchive(char alphabet, int day) {
        SyncService syncService = new SyncService(context);

        try {
            Calendar processStartedAt = Calendar.getInstance();

            List<Item> watchMatrix = syncService.getCodes();
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_YEAR, -day);
            Calendar endDate = Calendar.getInstance();
            List<Crawler> crawlers = new ArrayList<>();

            for (Item item : watchMatrix) {
                if (!item.getCode().startsWith(alphabet + "")) {
                    continue;
                }

                ScraperConfiguration config = Crawler.getScraperConfig(context, Crawler.CrawlType.DATA_ARCHIVE);
                Map params = new HashMap();
                dateFormat = new SimpleDateFormat(DSE_DATA_ARCHIVE_DATE_FORMAT);
                params.put("startDate", dateFormat.format(startDate.getTime()));
                params.put("endDate", dateFormat.format(endDate.getTime()));
                Crawler crawler = new Crawler(config, item, Crawler.CrawlType.DATA_ARCHIVE, params);
                crawler.start();
                crawlers.add(crawler);
            }

            int counter = 0;
            for (Crawler craw : crawlers) {
                craw.join();
                List<Item> items = (List<Item>) craw.getParams().get("items");
                if (items.size() == 0) {
                    System.out.println("Could not update for item: " + craw.getItem().getCode());
                    continue;
                }

                dao.open();
                dao.importItems(items);
                dao.close();
                System.out.println("[" + (++counter) + "]Import data archive finished for " + items.get(0).getCode());
            }

            Calendar processEndedAt = Calendar.getInstance();
            long elapsedTime = (processEndedAt.getTimeInMillis() - processStartedAt.getTimeInMillis()) / 1000;
            System.out.println("Time elapsed to sync " + day + " day archive for " + crawlers.size() + " item: " + (elapsedTime / 60) + " minutes " + (elapsedTime % 60) + " seconds");
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ImportService.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    public void importArchive(int day) {
        try {
            Calendar processStartedAt = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_YEAR, -day);
            Calendar endDate = Calendar.getInstance();
            //List<Crawler> crawlers = new ArrayList<>();

            ScraperConfiguration config = Crawler.getScraperConfig(context, Crawler.CrawlType.DATA_ARCHIVE);
            Map params = new HashMap();
            dateFormat = new SimpleDateFormat(DSE_DATA_ARCHIVE_DATE_FORMAT);
            params.put("startDate", dateFormat.format(startDate.getTime()));
            params.put("endDate", dateFormat.format(endDate.getTime()));
            Item item = new Item("All Instrument");
            Crawler crawler = new Crawler(config, item, Crawler.CrawlType.DATA_ARCHIVE, params);
            crawler.start();
                //crawlers.add(crawler);

            int counter = 0;
            crawler.join();
            List<Item> items = (List<Item>) crawler.getParams().get("items");
            List<Item> dsexItems = importDSEXArchive(day);
            System.out.println("dsex items: " + dsexItems.size());
            items.addAll(dsexItems);
                //filterOutUnneccessaryCodes(items, watchMatrix);

            if (items.size() > 0) {
                dao.open();
                dao.importItems(items);
                dao.close();
                //System.out.println("[" + (++counter) + "]Import data archive finished for " + items.get(0).getCode());
            }

            Calendar processEndedAt = Calendar.getInstance();
            long elapsedTime = (processEndedAt.getTimeInMillis() - processStartedAt.getTimeInMillis()) / 1000;
            System.out.println("Time elapsed to sync " + day + " day archive for " + items.size() + " item: " + (elapsedTime / 60) + " minutes " + (elapsedTime % 60) + " seconds");
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ImportService.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public List<Item> importDSEXArchive(int day) {
        List<Item> items = new ArrayList<>();

        try {
            Calendar processStartedAt = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_YEAR, -day);
            Calendar endDate = Calendar.getInstance();

            String path = Utils.getConfigFilesPath();
            ScraperConfiguration config = Crawler.getScraperConfig(path, Crawler.CrawlType.DSEX_DATA_ARCHIVE);
            Map params = new HashMap();
            dateFormat = new SimpleDateFormat(DSE_DATA_ARCHIVE_DATE_FORMAT);
            params.put("startDate", dateFormat.format(startDate.getTime()));
            params.put("endDate", dateFormat.format(endDate.getTime()));
            Item item = new Item("All Instrument");
            Crawler crawler = new Crawler(config, item, Crawler.CrawlType.DSEX_DATA_ARCHIVE, params);
            crawler.start();
            crawler.join();
            items = (List<Item>) crawler.getParams().get("items");
            Calendar processEndedAt = Calendar.getInstance();
            long elapsedTime = (processEndedAt.getTimeInMillis() - processStartedAt.getTimeInMillis()) / 1000;
            System.out.println("Time elapsed to sync " + day + " day archive for " + items.size() + " item: " + (elapsedTime / 60) + " minutes " + (elapsedTime % 60) + " seconds");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ImportService.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        return items;
    }

    @Deprecated
    public void importSingleCode(String code, InputStream inputStream) throws IOException, ParseException, SQLException, ClassNotFoundException {
        //List<Item> items = parseExcelFile(code, inputStream);
        List<Item> items = parseCSVFile(code, inputStream);
        dao.open();
        List<Item> existingItems = dao.getItems(code);
        for (Item item : existingItems) {
            items.remove(item);
        }

        dao.importItems(items);
        dao.close();
    }

    @Deprecated
    private List<Item> parseExcelFile(String code, InputStream inputStream) throws IOException, ParseException {
        if (code == null) {
            throw new RuntimeException("Import failed. Code should no be null");
        } else {
            code = code.toUpperCase();
        }

        List<Item> items = new ArrayList<>();

        try (HSSFWorkbook workbook = new HSSFWorkbook(inputStream)) {
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                //Order of the fields: Date,Open,High,Low,Close,Volume
                String dateStr = row.getCell(0).getStringCellValue();
                Date date = dateFormat.parse(dateStr);
                float open = Float.parseFloat(row.getCell(1).getStringCellValue());
                float high = Float.parseFloat(row.getCell(2).getStringCellValue());
                float low = Float.parseFloat(row.getCell(3).getStringCellValue());
                float close = Float.parseFloat(row.getCell(4).getStringCellValue());
                int volume = Integer.parseInt(row.getCell(5).getStringCellValue());

                Item item = new Item();
                item.setCode(code);
                item.setDate(date);
                item.setOpenPrice(open);
                item.setDayHigh(high);
                item.setDayLow(low);
                item.setClosePrice(close);
                item.setVolume(volume);
                items.add(item);
            }
        }

        return items;
    }

    @Deprecated
    private List<Item> parseCSVFile(String code, InputStream inputStream) throws IOException, ParseException {
        if (code == null) {
            throw new RuntimeException("Import failed. Code should no be null");
        } else {
            code = code.toUpperCase();
        }

        List<Item> items = new ArrayList<>();

        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            bReader.readLine(); //Leave headers
            while ((line = bReader.readLine()) != null) {
                String[] fields = line.split(",");
                String dateStr = fields[0];
                Date date = dateFormat.parse(dateStr);
                float open = Float.parseFloat(fields[1]);
                float high = Float.parseFloat(fields[2]);
                float low = Float.parseFloat(fields[3]);
                float close = Float.parseFloat(fields[4]);
                int volume = Integer.parseInt(fields[5]);

                Item item = new Item();
                item.setCode(code);
                item.setDate(date);
                item.setOpenPrice(open);
                item.setDayHigh(high);
                item.setDayLow(low);
                item.setClosePrice(close);
                item.setVolume(volume);
                items.add(item);
            }
        }

        return items;
    }
}
