package org.example.services.impl;

import org.example.dtos.*;
import org.example.dtos.responses.BookingStatusDetail;
import org.example.exceptions.WarningException;
import org.example.models.Booking;
import org.example.models.Venue;
import org.example.repositories.BookingRepository;
import org.example.services.BookingService;
import org.example.services.VenueService;

import java.awt.print.Book;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private VenueService venueService;
    public BookingServiceImpl(BookingRepository bookingRepository, VenueService venueService) {
        this.bookingRepository = bookingRepository;
        this.venueService = venueService;
    }

    public List<Venue> findAllVenueList(){
        return venueService.findVenuesWhereActive();
    }

    public List<BookedVenuesResponse> findBookedVenues(){
        return bookingRepository.findBookedVenues();
    }

    public boolean createBooking(BookingRequest bookingReq){
        Booking booking = new Booking();
        var check = bookingRepository.findBookingTransaction(bookingReq.getVenueId(), bookingReq.getDateBooked(), bookingReq.getStartTime(), bookingReq.getEndTime());
        Venue venue = venueService.findVenueId(bookingReq.getVenueId());

        if(check == null){

            if (bookingReq.getStartTime().getTime()  <  venue.getOpen().getTime() || bookingReq.getEndTime().getTime() > venue.getClose().getTime()){
                throw new WarningException("Pastikan waktu yang dipilih sesuai dengan operasional lapangan");
            }

            LocalDate nowDate = LocalDate.now();
            String datee = nowDate.format(DateTimeFormatter.ofPattern("MM"));
            LocalTime now = LocalTime.now();
            String time = now.format(DateTimeFormatter.ofPattern("hhmmss"));
            String id = "BK"+datee+"-"+time;

            // Set dateOrder
            LocalDate order = LocalDate.now();
            String date = order.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            booking.setId(id);
            booking.setDateOrder(date);
            booking.setUserId(bookingReq.getUserId());
            booking.setPrice(bookingReq.getPrice());
            booking.setDateBooked(bookingReq.getDateBooked());
            booking.setImageUrl(bookingReq.getImageUrl());
            booking.setStartTime(bookingReq.getStartTime());
            booking.setEndTime(bookingReq.getEndTime());
            booking.setVenueId(bookingReq.getVenueId());

            bookingRepository.createBooking(booking);
            return true;
        }


        throw new WarningException("Maaf jadwal sudah terisi, silahkan atur jadwal yang lain");

    }

    public List<BookingStatusDetail> findBookingStatus(String userId){
        List<BookingDetail> bookingDetails = bookingRepository.findBookedVenuesStatusByUserId(userId);
        List<BookingStatusDetail> resposes = new ArrayList<>();
        for (BookingDetail bookingDetail : bookingDetails) {
            String id = bookingDetail.getBookedId();
            String category = bookingDetail.getCategory();
            String venueName = bookingDetail.getVenueName();
            String dateOrder = bookingDetail.getDateOrder();
            String dateBooked = bookingDetail.getDateBooked();
            String startTime = bookingDetail.getStartTime();
            String endTime = bookingDetail.getEndTime();
            String status = "";
            if (bookingDetail.isConfirmed() != null){
                if (bookingDetail.isConfirmed() == true){
                    status = "Berhasil";
                } else if (bookingDetail.isConfirmed() == false){
                    status = "Ditolak";
                }
            } else {
                status = "Belum Terkonfirmasi";
            }

            resposes.add(new BookingStatusDetail(id, venueName, category, dateOrder, dateBooked, startTime, endTime, status ));
        }
        return resposes;
    }

    public List<VenueResponse> findAllVenue(){
        return  venueService.findAllVenues();
    }

    public List<VenueResponse> findVenueByCategory(String category){
        return venueService.findVenueByCategory(category);
    }

    public long fillPrice(Time startTime, Time endTime, String venueId){
        var venue = venueService.findVenueId(venueId);
        if (venue == null){
            return 0;
        }
        long venuePrice = venue.getPrice();
        long price = (long) (venuePrice * 0.3);
        long duration = endTime.getTime() - startTime.getTime();
        long hours = duration / (60 * 60 * 1000);
        long total = price * hours;
        return total;
    }

    public List<BookedVenuesResponse> findBookedVenuesByDateCategory(String date, String category){
        return bookingRepository.findBookedVenuesByDateCategory(date, category);
    }

    public List<BookingDetail> userHistoriesSuccess(String userId){
        return bookingRepository.userBookingHistoriesSuccess(userId);
    }
    public List<BookingDetail> userHistoriesSuccessByDate(String userId, int month, int year){
        return bookingRepository.userBookingHistoriesSuccessByDate(userId, month, year);
    }
    public List<BookingDetail> userHistoriesSuccessByYear(String userId, int year){
        return bookingRepository.userBookingHistoriesSuccessByYear(userId, year);
    }
    public BookingDetail reportBookingStruck(String bookingId){
        return bookingRepository.reportBookingStruck(bookingId);
    }
    public List<BookedVenuesResponse> findBookedVenuesByCategory(String category){
        return bookingRepository.findBookedVenuesByCategory(category);
    }
    public List<BookedVenuesResponse> findBookedVenuesByDate(String date){
        return bookingRepository.findBookedVenuesByDate(date);
    }

}
