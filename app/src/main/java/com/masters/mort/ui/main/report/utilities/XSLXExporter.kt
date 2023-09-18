package com.masters.mort.ui.main.report.utilities

import android.app.Activity
import com.masters.mort.R
import com.masters.mort.model.deceased_person.DeceasedPerson
import com.masters.mort.model.mortality.Mortality
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import kotlin.io.path.outputStream

class XSLXExporter {
    companion object{
        fun exportToXSLX(personsForExport: MutableList<DeceasedPerson>, mortalitiesForExport: MutableList<Mortality>, activity:Activity, userEmail:String) {
            if (personsForExport.isNotEmpty()) {

                //set up Excel WorkBook
                val workbook = XSSFWorkbook()
                val workSheet = workbook.createSheet()
                var rowIterator = 0
                val fields: Array<String> = activity.resources.getStringArray(R.array.fields)

                //Creating headers
                val headerRow: Row = workSheet.createRow(rowIterator)
                val column = workSheet.columnHelper
                rowIterator++
                headerRow.heightInPoints = 40.0F

                val cellStyle = workbook.createCellStyle()
                cellStyle.alignment = HorizontalAlignment.CENTER
                cellStyle.verticalAlignment = VerticalAlignment.CENTER
                cellStyle.topBorderColor = IndexedColors.BLACK.index
                cellStyle.bottomBorderColor = IndexedColors.BLACK.index
                cellStyle.rightBorderColor = IndexedColors.BLACK.index
                cellStyle.leftBorderColor = IndexedColors.BLACK.index

                var headerCellToSet: Cell
                fields.iterator().forEach {
                    headerCellToSet = headerRow.createCell(fields.indexOf(it))
                    column.setColWidth(fields.indexOf(it).toLong(), 40.0)
                    headerCellToSet.setCellValue(it)
                    headerCellToSet.cellStyle = cellStyle
                }

                //Filling data into cells
                var dataRow: Row
                var dataCell: Cell
                if (personsForExport.isNotEmpty()) {
                    for (patient in personsForExport) {
                        dataRow = workSheet.createRow(rowIterator)
                        dataRow.heightInPoints = 40.0F
                        fields.iterator().forEach {
                            dataCell = dataRow.createCell(fields.indexOf(it))
                            column.setColWidth(fields.indexOf(it).toLong(), 40.0)
                            dataCell.setCellValue(
                                if (it.contains("Name")) patient.name
                                else if (it.contains("Surname")) patient.surname
                                else if (it.contains("Birth")) patient.birthDate
                                else if (it.contains("Gender")) patient.gender
                                else patient.pid
                            )
                            dataCell.cellStyle = cellStyle
                        }
                        rowIterator++
                    }
                }

                rowIterator = 1
                if (mortalitiesForExport.isNotEmpty()) {
                    for (mortality in mortalitiesForExport) {
                        dataRow = workSheet.getRow(rowIterator)
                        fields.iterator().forEach {
                            if (it.contains("Death cause") || it.contains("Death date") || it.contains(
                                    "Death place"
                                )
                            ) {
                                dataCell = dataRow.createCell(fields.indexOf(it))
                                column.setColWidth(fields.indexOf(it).toLong(), 40.0)
                                dataCell.setCellValue(
                                    if (it.contains("Death cause")) mortality.mortalityCause
                                    else if (it.contains("Death date")) mortality.deathDateTime
                                    else mortality.deathPlace
                                )
                                dataCell.cellStyle = cellStyle
                            }
                        }
                        rowIterator++
                    }
                }

                //Making temporary file
                val tempFile = kotlin.io.path.createTempFile("MORT_Deceased_Persons", ".xlsx")
                workbook.write(tempFile.outputStream())
                workbook.close()

                EmailSender.sendEmail(tempFile, userEmail)
            }else{
                throw Exception("No data in database!")
            }
        }
    }
}